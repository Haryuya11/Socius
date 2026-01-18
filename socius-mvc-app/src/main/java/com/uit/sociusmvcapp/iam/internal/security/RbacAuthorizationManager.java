package com.uit.sociusmvcapp.iam.internal.security;

import com.uit.sociusmvcapp.iam.ApiPermissionService;
import com.uit.sociusmvcapp.iam.internal.dto.ApiPermissionDto;
import com.uit.sociusmvcapp.shared.constants.AuthConstant;
import java.util.Map;
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
 *
 * <p>Note: Public endpoints and CORS preflight (OPTIONS) requests are already handled by
 * SecurityConfig's permitAll() rules. This manager only handles authenticated requests.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RbacAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

  private final ApiPermissionService apiPermissionService;
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  @Override
  public AuthorizationDecision check(
      Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {

    String requestUri = context.getRequest().getRequestURI();
    String httpMethod = context.getRequest().getMethod();

    Authentication authentication = authenticationSupplier.get();

    // Check if user is authenticated
    if (authentication == null || !authentication.isAuthenticated()) {
      log.debug("Access denied: User not authenticated for [{} {}]", httpMethod, requestUri);
      return new AuthorizationDecision(false);
    }

    // Check if user has system.full permission (SYS_ADMIN)
    if (hasAuthority(authentication, AuthConstant.PERMISSION_SYSTEM_FULL)) {
      log.debug(
          "Access granted: User has system.full permission for [{} {}]", httpMethod, requestUri);
      return new AuthorizationDecision(true);
    }

    // Look up required permission from database
    ApiPermissionDto requiredPermission = apiPermissionService.matchRequest(httpMethod, requestUri);

    // If no permission mapping found, DENY access for security
    // All protected endpoints must have explicit permission mappings
    if (requiredPermission == null) {
      log.debug("Access denied: No permission mapping found for [{} {}]", httpMethod, requestUri);
      return new AuthorizationDecision(false);
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
   * Check if the user has the required permission. This method handles both global permissions and
   * scoped permissions (team/department).
   *
   * <p>For scope-sensitive resources (team/department), the user MUST have a scoped permission
   * matching the exact scope code from the URL. This ensures users can only access resources they
   * belong to. Global permissions for these resources are ignored - users must belong to the scope.
   *
   * <p>Note: SYS_ADMIN with 'system.full' permission bypasses this check entirely (handled earlier
   * in the authorization flow).
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

    // For team-scoped resources, user MUST belong to the specific team
    // Global permissions are not sufficient - scope membership is required
    if (AuthConstant.SCOPE_TEAM.equalsIgnoreCase(resource)) {
      String teamCode =
          extractPathVariable(
              requiredPermission.getUrlPattern(), requestUri, AuthConstant.PATH_VAR_TEAM_CODE);
      if (teamCode == null) {
        log.debug(
            "Access denied: Could not extract team code from URL [{}] with pattern [{}]",
            requestUri,
            requiredPermission.getUrlPattern());
        return false;
      }
      // Only check scoped authority (TEAM:{teamCode}:{permissionCode})
      String scopedAuthority =
          String.format(
              AuthConstant.SCOPED_AUTHORITY_FORMAT,
              AuthConstant.SCOPE_TEAM,
              teamCode,
              permissionCode);
      boolean hasAccess = hasAuthority(authentication, scopedAuthority);
      if (!hasAccess) {
        log.debug(
            "Access denied: User does not belong to team [{}] or lacks permission [{}]",
            teamCode,
            permissionCode);
      }
      return hasAccess;
    }

    // For department-scoped resources, user MUST belong to the specific department
    // Global permissions are not sufficient - scope membership is required
    if (AuthConstant.SCOPE_DEPARTMENT.equalsIgnoreCase(resource)) {
      String deptCode =
          extractPathVariable(
              requiredPermission.getUrlPattern(),
              requestUri,
              AuthConstant.PATH_VAR_DEPARTMENT_CODE);
      if (deptCode == null) {
        deptCode =
            extractPathVariable(
                requiredPermission.getUrlPattern(), requestUri, AuthConstant.PATH_VAR_DEPT_CODE);
      }
      if (deptCode == null) {
        log.debug(
            "Access denied: Could not extract department code from URL [{}] with pattern [{}]",
            requestUri,
            requiredPermission.getUrlPattern());
        return false;
      }
      // Only check scoped authority (DEPARTMENT:{deptCode}:{permissionCode})
      String scopedAuthority =
          String.format(
              AuthConstant.SCOPED_AUTHORITY_FORMAT,
              AuthConstant.SCOPE_DEPARTMENT,
              deptCode,
              permissionCode);
      boolean hasAccess = hasAuthority(authentication, scopedAuthority);
      if (!hasAccess) {
        log.debug(
            "Access denied: User does not belong to department [{}] or lacks permission [{}]",
            deptCode,
            permissionCode);
      }
      return hasAccess;
    }

    // For non-scoped resources (employee, role, notification, self, etc.)
    // Check global permission
    return hasAuthority(authentication, permissionCode);
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
      Map<String, String> variables =
          pathMatcher.extractUriTemplateVariables(urlPattern, requestUri);
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
