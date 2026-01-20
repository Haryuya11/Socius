package com.uit.sociusmvcapp.iam.internal.security;

import com.uit.sociusmvcapp.iam.ApiPermissionService;
import com.uit.sociusmvcapp.iam.TeamInfoGateway;
import com.uit.sociusmvcapp.iam.internal.dto.ApiPermissionDto;
import com.uit.sociusmvcapp.shared.constants.AuthConstant;
import java.util.Map;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
 * <p>Supports hierarchical scope: Department directors/managers can access teams within their
 * department even if they are not direct team members.
 *
 * <p>Note: Public endpoints and CORS preflight (OPTIONS) requests are already handled by
 * SecurityConfig's permitAll() rules. This manager only handles authenticated requests.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RbacAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

  private final ApiPermissionService apiPermissionService;
  private final TeamInfoGateway teamInfoGateway;
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  @Value("${server.servlet.context-path:}")
  private String contextPath;

  @Override
  public AuthorizationDecision check(
      Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {

    String requestUri = context.getRequest().getRequestURI();
    String httpMethod = context.getRequest().getMethod();

    Authentication authentication = authenticationSupplier.get();

    if (authentication == null || !authentication.isAuthenticated()) {
      log.debug("Access denied: User not authenticated for [{} {}]", httpMethod, requestUri);
      return new AuthorizationDecision(false);
    }

    if (hasAuthority(authentication, AuthConstant.PERMISSION_SYSTEM_FULL)) {
      log.debug(
          "Access granted: User has system.full permission for [{} {}]", httpMethod, requestUri);
      return new AuthorizationDecision(true);
    }

    ApiPermissionDto requiredPermission = apiPermissionService.matchRequest(httpMethod, requestUri);

    if (requiredPermission == null) {
      log.debug("Access denied: No permission mapping found for [{} {}]", httpMethod, requestUri);
      return new AuthorizationDecision(false);
    }

    String normalizedUri = normalizeUri(requestUri);

    boolean hasPermission = checkUserPermission(authentication, requiredPermission, normalizedUri);

    log.debug(
        "Access {}: User {} permission [{}] for [{} {}]",
        hasPermission ? "granted" : "denied",
        hasPermission ? "has" : "lacks",
        requiredPermission.getPermissionCode(),
        httpMethod,
        normalizedUri);

    return new AuthorizationDecision(hasPermission);
  }

  /**
   * Check if the user has the required permission. This method handles both global permissions and
   * scoped permissions (team/department).
   *
   * <p>Authorization flow for scoped resources (team/department):
   *
   * <ol>
   *   <li>If the URL pattern contains a scope path variable (e.g., {teamCode}, {departmentCode}),
   *       the user MUST have the scoped permission (SCOPE:CODE:PERM) to access. This ensures users
   *       can only access resources within scopes they belong to.
   *   <li>For endpoints WITHOUT scope path variables (e.g., /departments/search, /teams), global
   *       permissions are accepted.
   * </ol>
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
    String urlPattern = requiredPermission.getUrlPattern();

    // Team-scoped resource
    if (AuthConstant.SCOPE_TEAM.equalsIgnoreCase(resource)) {
      if (containsPathVariable(urlPattern, AuthConstant.PATH_VAR_TEAM_CODE)) {
        String teamCode =
            extractPathVariable(urlPattern, requestUri, AuthConstant.PATH_VAR_TEAM_CODE);
        if (teamCode == null) {
          log.debug(
              "Access denied: Could not extract team code from URL [{}] with pattern [{}]",
              requestUri,
              urlPattern);
          return false;
        }

        // 1. Check team-scoped permission (e.g., TEAM:T01:team.member.add)
        String scopedAuthority =
            String.format(
                AuthConstant.SCOPED_AUTHORITY_FORMAT,
                AuthConstant.SCOPE_TEAM,
                teamCode,
                permissionCode);
        if (hasAuthority(authentication, scopedAuthority)) {
          log.debug("Access granted via team scoped permission [{}]", scopedAuthority);
          return true;
        }

        // 2. Check if user belongs to the team AND has global permission
        if (hasAuthority(authentication, permissionCode)
            && userBelongsToScope(authentication, AuthConstant.SCOPE_TEAM, teamCode)) {
          log.debug(
              "Access granted: User belongs to team [{}] and has global permission [{}]",
              teamCode,
              permissionCode);
          return true;
        }

        // 3. HIERARCHICAL SCOPE: Check if user has department-scoped permission
        //    for the team's parent department (e.g., DEPT_DIR can access teams in their dept)
        String departmentCode = teamInfoGateway.getDepartmentCodeByTeamCode(teamCode);
        if (departmentCode != null) {
          String deptScopedAuthority =
              String.format(
                  AuthConstant.SCOPED_AUTHORITY_FORMAT,
                  AuthConstant.SCOPE_DEPARTMENT,
                  departmentCode,
                  permissionCode);
          if (hasAuthority(authentication, deptScopedAuthority)) {
            log.debug(
                "Access granted via hierarchical scope: User has department permission [{}] "
                    + "for team [{}] which belongs to department [{}]",
                deptScopedAuthority,
                teamCode,
                departmentCode);
            return true;
          }

          // Also check if user belongs to the parent department AND has global permission
          if (hasAuthority(authentication, permissionCode)
              && userBelongsToScope(
                  authentication, AuthConstant.SCOPE_DEPARTMENT, departmentCode)) {
            log.debug(
                "Access granted via hierarchical scope: User belongs to department [{}] "
                    + "and has global permission [{}] for team [{}]",
                departmentCode,
                permissionCode,
                teamCode);
            return true;
          }
        }

        log.debug(
            "Access denied: User does not belong to team [{}] or its parent department, "
                + "or lacks permission [{}]",
            teamCode,
            permissionCode);
        return false;
      } else {
        // No scope in URL (e.g., POST /teams, POST /teams/search)
        // Check global permission first
        if (hasAuthority(authentication, permissionCode)) {
          log.debug("Access granted via global permission [{}]", permissionCode);
          return true;
        }
        // Also check if user has the permission in ANY scope (for DEPT_DIR creating teams)
        if (hasAnyScopedPermission(authentication, permissionCode)) {
          log.debug(
              "Access granted: User has scoped permission [{}] in at least one team/department",
              permissionCode);
          return true;
        }
        return false;
      }
    }

    // Department-scoped resource
    if (AuthConstant.SCOPE_DEPARTMENT.equalsIgnoreCase(resource)) {
      boolean hasDeptCodeVar =
          containsPathVariable(urlPattern, AuthConstant.PATH_VAR_DEPARTMENT_CODE)
              || containsPathVariable(urlPattern, AuthConstant.PATH_VAR_DEPT_CODE);

      if (hasDeptCodeVar) {
        String deptCode =
            extractPathVariable(urlPattern, requestUri, AuthConstant.PATH_VAR_DEPARTMENT_CODE);
        if (deptCode == null) {
          deptCode = extractPathVariable(urlPattern, requestUri, AuthConstant.PATH_VAR_DEPT_CODE);
        }
        if (deptCode == null) {
          log.debug(
              "Access denied: Could not extract department code from URL [{}] with pattern [{}]",
              requestUri,
              urlPattern);
          return false;
        }

        String scopedAuthority =
            String.format(
                AuthConstant.SCOPED_AUTHORITY_FORMAT,
                AuthConstant.SCOPE_DEPARTMENT,
                deptCode,
                permissionCode);
        if (hasAuthority(authentication, scopedAuthority)) {
          log.debug("Access granted via department scoped permission [{}]", scopedAuthority);
          return true;
        }

        if (hasAuthority(authentication, permissionCode)
            && userBelongsToScope(authentication, AuthConstant.SCOPE_DEPARTMENT, deptCode)) {
          log.debug(
              "Access granted: User belongs to department [{}] and has global permission [{}]",
              deptCode,
              permissionCode);
          return true;
        }

        log.debug(
            "Access denied: User does not belong to department [{}] or lacks permission [{}]",
            deptCode,
            permissionCode);
        return false;
      } else {
        // No scope in URL (e.g., POST /departments, POST /departments/search)
        // Check global permission first
        if (hasAuthority(authentication, permissionCode)) {
          log.debug("Access granted via global permission [{}]", permissionCode);
          return true;
        }
        // Also check if user has the permission in ANY scope (for DEPT_DIR)
        if (hasAnyScopedPermission(authentication, permissionCode)) {
          log.debug(
              "Access granted: User has scoped permission [{}] in at least one department",
              permissionCode);
          return true;
        }
        return false;
      }
    }

    if ("task".equalsIgnoreCase(resource)) {
      if (hasAuthority(authentication, permissionCode)) {
        log.debug("Access granted via global permission [{}]", permissionCode);
        return true;
      }

      if (hasAnyScopedPermission(authentication, permissionCode)) {
        log.debug(
            "Access granted: User has scoped permission [{}] in at least one team/department",
            permissionCode);
        return true;
      }

      log.debug(
          "Access denied: User lacks permission [{}] globally or in any scope", permissionCode);
      return false;
    }

    return hasAuthority(authentication, permissionCode);
  }

  /**
   * Check if the user has the specified permission in ANY scope (team or department). This is
   * useful for endpoints that don't specify a scope in the URL but should still allow access if the
   * user has the permission in any of their assigned teams/departments.
   *
   * @param authentication the authentication object
   * @param permissionCode the permission code to check
   * @return true if the user has the permission in any scope
   */
  private boolean hasAnyScopedPermission(Authentication authentication, String permissionCode) {
    if (authentication.getAuthorities() == null) {
      return false;
    }

    String teamSuffix = AuthConstant.SCOPED_AUTHORITY_SEPARATOR + permissionCode;
    String deptSuffix = AuthConstant.SCOPED_AUTHORITY_SEPARATOR + permissionCode;
    String teamPrefix = AuthConstant.SCOPE_TEAM + AuthConstant.SCOPED_AUTHORITY_SEPARATOR;
    String deptPrefix = AuthConstant.SCOPE_DEPARTMENT + AuthConstant.SCOPED_AUTHORITY_SEPARATOR;

    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(
            a ->
                (a.startsWith(teamPrefix) && a.endsWith(teamSuffix))
                    || (a.startsWith(deptPrefix) && a.endsWith(deptSuffix)));
  }

  /**
   * Check if a URL pattern contains a specific path variable.
   *
   * @param urlPattern the URL pattern (e.g., /teams/{teamCode}/employees)
   * @param variableName the variable name to check for (e.g., "teamCode")
   * @return true if the pattern contains the variable
   */
  private boolean containsPathVariable(String urlPattern, String variableName) {
    return urlPattern != null && urlPattern.contains("{" + variableName + "}");
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

  /**
   * Check if the user belongs to a specific scope (team or department). This checks if the user has
   * ANY authority that starts with "SCOPE:CODE:" pattern, meaning they have at least one permission
   * within that scope.
   *
   * @param authentication the authentication object
   * @param scopeType the scope type (e.g., "TEAM", "DEPARTMENT")
   * @param scopeCode the scope code (e.g., "T01", "DEPT01")
   * @return true if the user has at least one scoped authority for the given scope
   */
  private boolean userBelongsToScope(
      Authentication authentication, String scopeType, String scopeCode) {
    if (authentication.getAuthorities() == null) {
      return false;
    }
    String scopePrefix =
        String.format(AuthConstant.SCOPED_AUTHORITY_FORMAT, scopeType, scopeCode, "");
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(a -> a.startsWith(scopePrefix));
  }

  /**
   * Normalize the request URI by removing the context path prefix.
   *
   * @param requestUri the full request URI
   * @return the normalized URI without context path
   */
  private String normalizeUri(String requestUri) {
    if (contextPath != null && !contextPath.isEmpty() && requestUri.startsWith(contextPath)) {
      return requestUri.substring(contextPath.length());
    }
    return requestUri;
  }
}
