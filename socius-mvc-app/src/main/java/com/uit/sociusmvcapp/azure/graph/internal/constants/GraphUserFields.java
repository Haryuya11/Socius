package com.uit.sociusmvcapp.azure.graph.internal.constants;

/**
 * Constants for Microsoft Graph User fields.
 *
 * <p>Use these constants when building Graph API queries like $select or $filter.
 */
public final class GraphUserFields {

  private GraphUserFields() {}

  /** The unique identifier for the user object. */
  public static final String ID = "id";

  /** The name displayed for the user in address books. */
  public static final String DISPLAY_NAME = "displayName";

  /** The user's first name (given name). */
  public static final String GIVEN_NAME = "givenName";

  /** The user's last name (surname or family name). */
  public static final String SURNAME = "surname";

  /** The user's primary SMTP email address. */
  public static final String MAIL = "mail";

  /** The user principal name (UPN), required for sign-in. */
  public static final String USER_PRINCIPAL_NAME = "userPrincipalName";

  /** The name of the department in which the user works. */
  public static final String DEPARTMENT = "department";

  /** A boolean indicating whether the user account is enabled for sign-in. */
  public static final String ACCOUNT_ENABLED = "accountEnabled";

  /** The timestamp of the user's last password change. */
  public static final String LAST_PASSWORD_CHANGE_DATE_TIME = "lastPasswordChangeDateTime";
}
