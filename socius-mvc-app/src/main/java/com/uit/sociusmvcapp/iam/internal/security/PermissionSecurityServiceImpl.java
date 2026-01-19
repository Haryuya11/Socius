package com.uit.sociusmvcapp.iam.internal.security;

import com.uit.sociusmvcapp.iam.PermissionSecurityService;
import com.uit.sociusmvcapp.shared.constants.AuthConstant;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Implementation of PermissionSecurityService for evaluating user permissions.
 *
 * <p>This bean is named "permissionService" to be accessible via Spring Expression Language (SpEL)
 * within security annotations.
 */
@Service("permissionService")
public class PermissionSecurityServiceImpl implements PermissionSecurityService {

  /**
   * Checks if the current user possesses a specific global permission. Global permissions are not
   * scoped to a specific resource and apply system-wide.
   *
   * @param permissionCode The code of the permission to check (e.g., 'employee.view.basic').
   * @return {@code true} if the user has the required global permission, {@code false} otherwise.
   */
  @Override
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
  @Override
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
   * Checks if the current user possesses a specific permission within a specific scope.
   *
   * @param scopeType The type of scope (e.g., 'TEAM', 'DEPARTMENT').
   * @param scopeCode The code of the scope (e.g., 'T01', 'DEPT01').
   * @param permissionCode The code of the permission to check (e.g., 'task.update').
   * @return {@code true} if the user has the required permission in the specified scope, {@code
   *     false} otherwise.
   */
  @Override
  public boolean hasScopedPermission(String scopeType, String scopeCode, String permissionCode) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return false;
    }

    // Build the expected scoped authority (e.g., TEAM:T01:task.update)
    String scopedAuthority =
        String.format(AuthConstant.SCOPED_AUTHORITY_FORMAT, scopeType, scopeCode, permissionCode);

    return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(scopedAuthority));
  }

  /**
   * Checks if the current user belongs to a specific scope.
   *
   * @param scopeType The type of scope (e.g., 'TEAM', 'DEPARTMENT').
   * @param scopeCode The code of the scope (e.g., 'T01', 'DEPT01').
   * @return {@code true} if the user belongs to the specified scope, {@code false} otherwise.
   */
  @Override
  public boolean belongsToScope(String scopeType, String scopeCode) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return false;
    }

    // Check if user has any authority that starts with SCOPE_TYPE:SCOPE_CODE:
    String scopePrefix =
        String.format(AuthConstant.SCOPED_AUTHORITY_FORMAT, scopeType, scopeCode, "");

    return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().startsWith(scopePrefix));
  }

  /**
   * Checks if the current user is a system administrator.
   *
   * @return {@code true} if the user is a system administrator, {@code false} otherwise.
   */
  @Override
  public boolean isSystemAdmin() {
    return hasGlobalPermission(AuthConstant.PERMISSION_SYSTEM_FULL);
  }
}
