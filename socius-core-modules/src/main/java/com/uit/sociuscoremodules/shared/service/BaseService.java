package com.uit.sociuscoremodules.shared.service;

import com.uit.sociuscoremodules.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Base interface for all service-layer classes, providing common helper methods for creating and
 * throwing standardized {@link BusinessException} instances.
 *
 * <p>This interface enforces a consistent pattern of exception handling across all services in the
 * application. It integrates with i18n message codes to support localized error responses.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * if (user == null) {
 *   throw notFound("error.user.notFound", userId);
 * }
 * }</pre>
 */
public interface BaseService {

  /**
   * Creates a {@link BusinessException} with the specified HTTP status, i18n code, and optional
   * message arguments.
   *
   * @param status the HTTP status to associate with this exception
   * @param code the i18n message code (must correspond to a message key)
   * @param args optional message arguments for parameterized i18n messages (nullable)
   * @return a {@link BusinessException} containing the provided data
   */
  BusinessException createBusinessException(HttpStatus status, String code, Object... args);

  /**
   * Creates a {@link BusinessException} representing a <b>400 Bad Request</b> error. Used for
   * validation or input-related issues.
   *
   * @param code the i18n message code
   * @param args optional message arguments (nullable)
   * @return a {@link BusinessException} with status {@link HttpStatus#BAD_REQUEST}
   */
  BusinessException badRequest(String code, Object... args);

  /**
   * Creates a {@link BusinessException} representing a <b>404 Not Found</b> error. Used when a
   * requested resource or entity is missing.
   *
   * @param code the i18n message code
   * @param args optional message arguments (nullable)
   * @return a {@link BusinessException} with status {@link HttpStatus#NOT_FOUND}
   */
  BusinessException notFound(String code, Object... args);

  /**
   * Creates a {@link BusinessException} representing a <b>500 Internal Server Error</b>. Used for
   * unexpected or unhandled failures.
   *
   * @param code the i18n message code
   * @param args optional message arguments (nullable)
   * @return a {@link BusinessException} with status {@link HttpStatus#INTERNAL_SERVER_ERROR}
   */
  BusinessException internalError(String code, Object... args);
}
