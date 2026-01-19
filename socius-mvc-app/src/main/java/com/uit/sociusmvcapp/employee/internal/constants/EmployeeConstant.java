package com.uit.sociusmvcapp.employee.internal.constants;

/** Constants related to Employee operations. */
public final class EmployeeConstant {
  /** Private constructor to prevent instantiation. */
  private EmployeeConstant() {}

  /** Default password for new employees. */
  public static final String DEFAULT_PASSWORD = "deV123123*";

  /** Sign-in type for email address identities. */
  public static final String SIGN_IN_TYPE_EMAIL_ADDRESS = "emailAddress";

  /** Key for client ID. */
  public static final String CLIENT_ID = "clientId";

  /** Permission code for viewing employee salary. */
  public static final String PERMISSION_VIEW_SALARY = "employee.view.salary";

  /** Permission code for system full access (SYS_ADMIN). */
  public static final String PERMISSION_SYSTEM_FULL = "system.full";

  /**
   * Masked salary value returned when user doesn't have permission to view salary. Using -1 to
   * indicate restricted/hidden value.
   */
  public static final Long MASKED_SALARY = -1L;
}
