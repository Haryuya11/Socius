package com.uit.sociusmvcapp.iam;

/**
 * Service interface for evaluating user permissions.
 *
 * <p>This is the public API for permission checking that can be used by other modules.
 */
public interface PermissionSecurityService {

  /**
   * Checks if the current user possesses a specific global permission. Global permissions are not
   * scoped to a specific resource and apply system-wide.
   *
   * @param permissionCode The code of the permission to check (e.g., 'employee.view.basic').
   * @return {@code true} if the user has the required global permission, {@code false} otherwise.
   */
  boolean hasGlobalPermission(String permissionCode);

  /**
   * Checks if the current user possesses a specific permission in any scope (global, department, or
   * team). This is useful for checking if the user has a permission regardless of which
   * department/team they have it in.
   *
   * @param permissionCode The code of the permission to check (e.g., 'employee.view.salary').
   * @return {@code true} if the user has the required permission in any scope, {@code false}
   *     otherwise.
   */
  boolean hasAnyPermission(String permissionCode);
}
