package com.uit.sociusmvcapp.iam.internal.security;

import com.uit.sociusmvcapp.shared.constants.AuthConstant;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for evaluating granular, scoped-based user permissions.
 *
 * <p>This bean is named "permissionService" to be accessible via Spring Expression Language (SpEL)
 * within security annotations.
 *
 * <p>Usage Example: {@code @PreAuthorize("@permissionService.hasTeamPermission(#teamCode,
 * 'task.create')")}
 */
@Service("permissionService")
public class PermissionSecurityService {

  /**
   * Checks if the current user possesses a specific global permission. Global permissions are not
   * scoped to a specific resource and apply system-wide.
   *
   * @param permissionCode The code of the permission to check (e.g., 'employee.view.basic').
   * @return {@code true} if the user has the required global permission, {@code false} otherwise.
   */
  public boolean hasGlobalPermission(String permissionCode) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return false;
    }

    return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(permissionCode));
  }

  /**
   * Checks if the current user possesses a specific permission in any scope (department or team).
   * This is useful for checking if the user has a permission regardless of which department/team
   * they have it in.
   *
   * @param permissionCode The code of the permission to check (e.g., 'employee.view.salary').
   * @return {@code true} if the user has the required permission in any scope, {@code false}
   *     otherwise.
   */
  public boolean hasAnyPermission(String permissionCode) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return false;
    }

    // Check global permission first
    if (hasGlobalPermission(permissionCode)) {
      return true;
    }

    // Check scoped permissions (SCOPE:RESOURCE:PERMISSION format)
    return auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().endsWith(":" + permissionCode));
  }

  /**
   * Checks if the current user possesses a specific permission within a specific Team.
   *
   * @param teamCode The unique identifier of the team (Resource ID).
   * @param permissionCode The code of the permission to check (e.g., 'task.create').
   * @return {@code true} if the user has the required permission in the specified team, {@code
   *     false} otherwise.
   */
  public boolean hasTeamPermission(String teamCode, String permissionCode) {
    return hasScopedPermission(AuthConstant.SCOPE_TEAM, teamCode, permissionCode);
  }

  /**
   * Checks if the current user possesses a specific permission within a specific Department.
   *
   * <p>Example: Checking if a user can view salaries ('employee.view.salary') in the IT department
   * ('DEPT_IT').
   *
   * @param deptCode The unique identifier of the department (Resource ID).
   * @param permissionCode The code of the permission to check.
   * @return {@code true} if the user has the required permission in the specified department,
   *     {@code false} otherwise.
   */
  public boolean hasDeptPermission(String deptCode, String permissionCode) {
    return hasScopedPermission(AuthConstant.SCOPE_DEPARTMENT, deptCode, permissionCode);
  }

  /**
   * Core logic to validate scoped authorities against the Security Context.
   *
   * <p>It constructs the authority string using the format: {@code
   * SCOPE:RESOURCE_ID:PERMISSION_CODE} (e.g., {@code TEAM:T01:task.create}) and checks if the
   * authenticated user holds this authority.
   *
   * @param scope The scope type (e.g., TEAM, DEPARTMENT).
   * @param resourceCode The target resource identifier.
   * @param permissionCode The specific permission action.
   * @return {@code true} if a matching authority is found in the user's granted authorities.
   */
  private boolean hasScopedPermission(String scope, String resourceCode, String permissionCode) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return false;
    }

    // Target Authority Format: SCOPE:RESOURCE:PERMISSION
    // Example: TEAM:TEAM_01:task.create
    String requiredAuthority = String.format("%s:%s:%s", scope, resourceCode, permissionCode);

    return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(requiredAuthority));
  }
}
