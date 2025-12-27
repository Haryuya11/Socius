package com.uit.sociusmvcapp.shared.service;

import com.uit.sociusmvcapp.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Base service implementation providing common functionality for handling business exceptions with
 * internationalization support.
 */
public final class ExceptionFactory {

  /** Private constructor to prevent instantiation. */
  private ExceptionFactory() {}

  /**
   * Creates a {@link BusinessException} with the specified HTTP status, i18n code, and optional
   * message arguments.
   *
   * @param status the HTTP status to associate with this exception
   * @param code the i18n message code (must correspond to a message key)
   * @param args optional message arguments for parameterized i18n messages (nullable)
   * @return a {@link BusinessException} containing the provided data
   */
  public static BusinessException createBusinessException(
      HttpStatus status, String code, Object... args) {
    return new BusinessException(status, code, args == null ? new Object[] {} : args);
  }

  /**
   * Creates a {@link BusinessException} representing a <b>400 Bad Request</b> error. Used for
   * validation or input-related issues.
   *
   * @param code the i18n message code
   * @param args optional message arguments (nullable)
   * @return a {@link BusinessException} with status {@link HttpStatus#BAD_REQUEST}
   */
  public static BusinessException badRequest(String code, Object... args) {
    return createBusinessException(HttpStatus.BAD_REQUEST, code, args);
  }

  /**
   * Creates a {@link BusinessException} representing a <b>404 Not Found</b> error. Used when a
   * requested resource or entity is missing.
   *
   * @param code the i18n message code
   * @param args optional message arguments (nullable)
   * @return a {@link BusinessException} with status {@link HttpStatus#NOT_FOUND}
   */
  public static BusinessException notFound(String code, Object... args) {
    return createBusinessException(HttpStatus.NOT_FOUND, code, args);
  }

  /**
   * Creates a {@link BusinessException} representing a <b>500 Internal Server Error</b>. Used for
   * unexpected or unhandled failures.
   *
   * @param code the i18n message code
   * @param args optional message arguments (nullable)
   * @return a {@link BusinessException} with status {@link HttpStatus#INTERNAL_SERVER_ERROR}
   */
  public static BusinessException internalError(String code, Object... args) {
    return createBusinessException(HttpStatus.INTERNAL_SERVER_ERROR, code, args);
  }
}
