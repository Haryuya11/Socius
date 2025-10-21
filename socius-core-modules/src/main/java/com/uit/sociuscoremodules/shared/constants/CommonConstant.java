package com.uit.sociuscoremodules.shared.constants;

/** General constants for the application. */
public final class CommonConstant {

  /** Private constructor to prevent instantiation. */
  private CommonConstant() {}

  /** URL template for fetching JSON Web Key Set from Microsoft identity platform. */
  public static final String JSON_WEB_KEY_SET_URL =
      "https://login.microsoftonline.com/%s/discovery/v2.0/keys";
}
