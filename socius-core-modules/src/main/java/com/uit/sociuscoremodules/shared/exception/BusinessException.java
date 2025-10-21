package com.uit.sociuscoremodules.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception class for business logic errors. This exception includes an HTTP status, an
 * error code for i18n message lookup, and optional arguments for the i18n message.
 */
@Getter
public class BusinessException extends RuntimeException {

  /** HTTP status code associated with the exception. */
  private final HttpStatus status;

  /** Error code for i18n message lookup. */
  private final String code;

  /** Arguments for the i18n message. */
  private final transient Object[] args;

  /**
   * Constructor for BusinessException.
   *
   * @param status HTTP status code
   * @param code Error code for i18n message lookup
   * @param args Arguments for the i18n message
   */
  public BusinessException(HttpStatus status, String code, Object... args) {
    super("");
    this.status = status;
    this.code = code;
    this.args = args;
  }
}
