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

  /**
   * Checks if the current user possesses a specific permission within a specific scope. This is
   * used for resource-level authorization where the user must have permission in the exact scope
   * that owns the resource.
   *
   * @param scopeType The type of scope (e.g., 'TEAM', 'DEPARTMENT').
   * @param scopeCode The code of the scope (e.g., 'T01', 'DEPT01').
   * @param permissionCode The code of the permission to check (e.g., 'task.update').
   * @return {@code true} if the user has the required permission in the specified scope, {@code
   *     false} otherwise.
   */
  boolean hasScopedPermission(String scopeType, String scopeCode, String permissionCode);

  /**
   * Checks if the current user belongs to a specific scope. A user belongs to a scope if they have
   * at least one authority that starts with 'SCOPE_TYPE:SCOPE_CODE:'.
   *
   * @param scopeType The type of scope (e.g., 'TEAM', 'DEPARTMENT').
   * @param scopeCode The code of the scope (e.g., 'T01', 'DEPT01').
   * @return {@code true} if the user belongs to the specified scope, {@code false} otherwise.
   */
  boolean belongsToScope(String scopeType, String scopeCode);

  /**
   * Checks if the current user is a system administrator (has 'system.full' permission).
   *
   * @return {@code true} if the user is a system administrator, {@code false} otherwise.
   */
  boolean isSystemAdmin();
}
