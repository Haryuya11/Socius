package com.uit.sociuscoremodules.employee.constants;

/** RoleConstants class containing constant values related to roles and permissions. */
public final class RoleConstants {

  /** Private constructor to prevent instantiation. */
  private RoleConstants() {}

  /** Role Scope Systems. */
  public static final String ROLE_TYPE_SYSTEM = "SYSTEM";

  /** Role Scope Departments. */
  public static final String ROLE_TYPE_DEPARTMENT = "DEPARTMENT";

  /** Role Scope Teams. */
  public static final String ROLE_TYPE_TEAM = "TEAM";

  /** Separator for scope keys. */
  public static final String SCOPE_KEY_SEPARATOR = ":";
}
