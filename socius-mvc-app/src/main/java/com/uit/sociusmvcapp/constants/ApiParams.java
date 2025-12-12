package com.uit.sociusmvcapp.constants;

/** ApiParams holds constants for API parameter names. */
public final class ApiParams {
  private ApiParams() {}

  /** Base path for employee-related API endpoints. */
  public static final String EMPLOYEES = "/employees";

  /** Path for employee profile endpoint. */
  public static final String PROFILE = "/profile";

  /** Path parameter for client ID. */
  public static final String CLIENT_ID_PARAM = "/{clientId}";

  /** Path for change password endpoint. */
  public static final String CHANGE_PASSWORD = "/change-password";

  public static final String SEARCH = "/search";
}
