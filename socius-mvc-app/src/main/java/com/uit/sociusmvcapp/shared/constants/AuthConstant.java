package com.uit.sociusmvcapp.shared.constants;

/** Constants used for authorization scopes and resources. */
public final class AuthConstant {

  /** Private constructor to prevent instantiation. */
  private AuthConstant() {}

  /** Authorization scope constants. */
  public static final String SCOPE_GLOBAL = "GLOBAL";

  /** System scope. */
  public static final String SCOPE_SYSTEM = "SYSTEM";

  /** Team scope. */
  public static final String SCOPE_TEAM = "TEAM";

  /** Department scope. */
  public static final String SCOPE_DEPARTMENT = "DEPARTMENT";

  /** Resource all. */
  public static final String RESOURCE_ALL = "ALL";

  /** System full permission code for admin access. */
  public static final String PERMISSION_SYSTEM_FULL = "system.full";

  /** Path variable name for team code. */
  public static final String PATH_VAR_TEAM_CODE = "teamCode";

  /** Path variable name for department code. */
  public static final String PATH_VAR_DEPARTMENT_CODE = "departmentCode";

  /** Path variable name for department code (short form). */
  public static final String PATH_VAR_DEPT_CODE = "deptCode";

  /** Scoped authority format pattern. */
  public static final String SCOPED_AUTHORITY_FORMAT = "%s:%s:%s";

  /** Separator used in scoped authority strings (e.g., TEAM:T01:task.update). */
  public static final String SCOPED_AUTHORITY_SEPARATOR = ":";
}
