package com.uit.sociusmvcapp.iam.internal.security;

import com.uit.sociusmvcapp.iam.ApiPermissionService;
import com.uit.sociusmvcapp.iam.internal.dto.ApiPermissionDto;
import com.uit.sociusmvcapp.shared.constants.AuthConstant;
import com.uit.sociusmvcapp.shared.constants.SecurityConstant;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * Database-driven RBAC Authorization Manager. Resolves required permissions from the database based
 * on the incoming request's HTTP method and URL pattern.
 *
 * <p>This implementation does not hardcode permissions in code - all permission mappings are
 * fetched from the api_permissions table.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RbacAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

  private final ApiPermissionService apiPermissionService;
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  private static final String SYSTEM_FULL_PERMISSION = "system.full";

  @Override
  public AuthorizationDecision check(
      Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {

    String requestUri = context.getRequest().getRequestURI();
    String httpMethod = context.getRequest().getMethod();

    // Skip OPTIONS requests (CORS preflight)
    if ("OPTIONS".equalsIgnoreCase(httpMethod)) {
      return new AuthorizationDecision(true);
    }

    // Skip public endpoints
    if (isPublicEndpoint(requestUri)) {
      return new AuthorizationDecision(true);
    }

    Authentication authentication = authenticationSupplier.get();

    // Check if user is authenticated
    if (authentication == null || !authentication.isAuthenticated()) {
      log.debug("Access denied: User not authenticated for [{} {}]", httpMethod, requestUri);
      return new AuthorizationDecision(false);
    }

    // Check if user has system.full permission (SYS_ADMIN)
    if (hasAuthority(authentication, SYSTEM_FULL_PERMISSION)) {
      log.debug(
          "Access granted: User has system.full permission for [{} {}]", httpMethod, requestUri);
      return new AuthorizationDecision(true);
    }

    // Look up required permission from database
    ApiPermissionDto requiredPermission = apiPermissionService.matchRequest(httpMethod, requestUri);

    // If no permission mapping found, allow authenticated access
    // This means endpoints without explicit permission mapping are accessible to any authenticated
    // user
    if (requiredPermission == null) {
      log.debug(
          "No permission mapping found for [{} {}], allowing authenticated access",
          httpMethod,
          requestUri);
      return new AuthorizationDecision(true);
    }

    // Check if user has the required permission
    boolean hasPermission = checkUserPermission(authentication, requiredPermission, requestUri);

    log.debug(
        "Access {}: User {} permission [{}] for [{} {}]",
        hasPermission ? "granted" : "denied",
        hasPermission ? "has" : "lacks",
        requiredPermission.getPermissionCode(),
        httpMethod,
        requestUri);

    return new AuthorizationDecision(hasPermission);
  }

  /**
   * Check if the request URI matches any public endpoint pattern.
   *
   * @param requestUri the request URI
   * @return true if the endpoint is public
   */
  private boolean isPublicEndpoint(String requestUri) {
    for (String pattern : SecurityConstant.PUBLIC_ENDPOINTS) {
      if (pathMatcher.match(pattern, requestUri)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if the user has the required permission. This method handles both global permissions and
   * scoped permissions (team/department).
   *
   * @param authentication the authentication object
   * @param requiredPermission the required permission
   * @param requestUri the request URI for scope extraction
   * @return true if the user has the required permission
   */
  private boolean checkUserPermission(
      Authentication authentication, ApiPermissionDto requiredPermission, String requestUri) {

    String permissionCode = requiredPermission.getPermissionCode();
    String resource = requiredPermission.getResource();

    // First, check for global/system permission
    if (hasAuthority(authentication, permissionCode)) {
      return true;
    }

    // For team-scoped permissions, extract team code and check scoped authority
    if (AuthConstant.SCOPE_TEAM.equalsIgnoreCase(resource)) {
      String teamCode =
          extractPathVariable(requiredPermission.getUrlPattern(), requestUri, "teamCode");
      if (teamCode != null) {
        String scopedAuthority =
            String.format("%s:%s:%s", AuthConstant.SCOPE_TEAM, teamCode, permissionCode);
        return hasAuthority(authentication, scopedAuthority);
      }
    }

    // For department-scoped permissions, extract department code and check scoped authority
    if (AuthConstant.SCOPE_DEPARTMENT.equalsIgnoreCase(resource)) {
      String deptCode =
          extractPathVariable(requiredPermission.getUrlPattern(), requestUri, "departmentCode");
      if (deptCode == null) {
        deptCode = extractPathVariable(requiredPermission.getUrlPattern(), requestUri, "deptCode");
      }
      if (deptCode != null) {
        String scopedAuthority =
            String.format("%s:%s:%s", AuthConstant.SCOPE_DEPARTMENT, deptCode, permissionCode);
        return hasAuthority(authentication, scopedAuthority);
      }
    }

    return false;
  }

  /**
   * Check if the authentication has a specific authority.
   *
   * @param authentication the authentication object
   * @param authority the authority to check
   * @return true if the user has the authority
   */
  private boolean hasAuthority(Authentication authentication, String authority) {
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(a -> a.equals(authority));
  }

  /**
   * Extract a path variable value from the request URI based on the URL pattern.
   *
   * @param urlPattern the URL pattern with placeholders (e.g., /teams/{teamCode}/tasks)
   * @param requestUri the actual request URI (e.g., /teams/T01/tasks)
   * @param variableName the name of the variable to extract (e.g., "teamCode")
   * @return the extracted value or null if not found
   */
  private String extractPathVariable(String urlPattern, String requestUri, String variableName) {
    try {
      var variables = pathMatcher.extractUriTemplateVariables(urlPattern, requestUri);
      return variables.get(variableName);
    } catch (Exception e) {
      log.debug(
          "Failed to extract path variable [{}] from URI [{}]: {}",
          variableName,
          requestUri,
          e.getMessage());
      return null;
    }
  }
}
