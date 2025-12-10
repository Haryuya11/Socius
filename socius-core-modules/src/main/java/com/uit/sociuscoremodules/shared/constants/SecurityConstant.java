package com.uit.sociuscoremodules.shared.constants;

import java.util.List;

/** Constants related to authentication, authorization, and security configurations. */
public final class SecurityConstant {

  private SecurityConstant() {}

  /** Timeout in milliseconds for establishing a connection to the JWK Set URL. */
  public static final Integer JWK_CONNECT_TIMEOUT_MILLIS = 2000;

  /** Timeout in milliseconds for reading data from the JWK Set URL. */
  public static final Integer JWK_READ_TIMEOUT_MILLIS = 2000;

  /** Size limit in bytes for the JWK Set resource to prevent downloading overly large files. */
  public static final Integer JWK_SIZE_LIMIT_BYTES = 100_000; // 100 KB

  /** The duration in days for which the cached JWK Set is considered valid (Time-To-Live). */
  public static final Long CACHE_TIME_TO_LIVE_DAYS = 1L;

  /** The refresh interval in hours for the cached JWK Set before it expires. */
  public static final Long CACHE_REFRESH_TIMEOUT_HOURS = 1L;

  /** URL template for fetching JSON Web Key Set from Microsoft identity platform. */
  public static final String JSON_WEB_KEY_SET_URL =
      "https://%s.ciamlogin.com/%s/discovery/v2.0/keys";

  /** Issuer URI template for validating JWT tokens from Microsoft identity platform. */
  public static final String ISSUER_URI_FORMAT = "https://%s.ciamlogin.com/%s/v2.0";

  /** Claim name for the Object ID in JWT tokens. */
  public static final String OID_CLAIM_NAME = "oid";

  /** Default role assigned to authenticated users. */
  public static final String DEFAULT_ROLE_USER = "ROLE_USER";

  /** HTTP Authorization header name. */
  public static final String AUTHORIZATION_HEADER = "Authorization";

  /** Prefix for Bearer tokens in the Authorization header. */
  public static final String BEARER_PREFIX = "Bearer ";

  /** Length of the Bearer token prefix. */
  public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

  /** Query parameter name for tokens in URLs. */
  public static final String TOKEN_PARAM_NAME = "token";

  /** Query parameter prefix for tokens in URLs. */
  public static final String START_WITH_TOKEN = "token=";

  /** Index position to extract the token value from the query parameter. */
  public static final int TOKEN_INDEX = START_WITH_TOKEN.length();

  /** Allowed origin patterns for CORS requests. */
  public static final List<String> CORS_ALLOWED_ORIGIN_PATTERNS = List.of("*");

  /** Allowed HTTP methods in CORS requests. */
  public static final List<String> CORS_ALLOWED_METHODS =
      List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");

  /** Allowed headers in CORS requests. */
  public static final List<String> CORS_ALLOWED_HEADERS = List.of("*");

  /** Whether to allow credentials (cookies, authorization headers, etc.) in CORS requests. */
  public static final boolean CORS_ALLOW_CREDENTIALS = true;

  /** Max age (in seconds) for CORS preflight response caching. */
  public static final Long MAX_AGE = 3600L; // 1 hour

  /** Wildcard pattern to match all paths. */
  public static final String ALL_PATHS = "/**";

  /** Public endpoints that do not require authentication. */
  public static final List<String> PUBLIC_ENDPOINTS =
      List.of(
          "/health",
          "/ping",
          "/favicon.ico",
          "/robots.txt",
          "/error",
          "/actuator/health",
          "/actuator/health/**",
          "/actuator/info",
          "/teams/**",
          "/teams/employees/**");
}
