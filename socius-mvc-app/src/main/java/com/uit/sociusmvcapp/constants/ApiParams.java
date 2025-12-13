package com.uit.sociusmvcapp.constants;

/** ApiParams holds constants for API parameter names. */
public final class ApiParams {
  private ApiParams() {}

  /** Base path for employee-related API endpoints. */
  public static final String EMPLOYEES_PREFIX = "/employees";

  /** Base path for role-related API endpoints. */
  public static final String ROLES_PREFIX = "/roles";

  /** Path for employee profile endpoint. */
  public static final String PROFILE = "/profile";

  /** Path parameter for client ID. */
  public static final String CLIENT_ID_PARAM = "/{clientId}";

  /** Path for change password endpoint. */
  public static final String CHANGE_PASSWORD = "/change-password";

  /** Path for search endpoint. */
  public static final String SEARCH = "/search";

  /** Path parameter for role code. */
  public static final String ROLE_CODE_PARAM = "/{roleCode}";

  /** Path parameter for role type. */
  public static final String ROLE_TYPE_PARAM = "/type/{roleType}";
}
