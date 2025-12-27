package com.uit.sociusmvcapp.shared.exception;

import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.Response;
import com.uit.sociusmvcapp.shared.service.I18nService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler handles exceptions thrown by controllers and provides appropriate HTTP
 * responses.
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

  /** I18nService for internationalization messages. */
  private final I18nService i18nService;

  /**
   * Handle BusinessException and return a structured error response.
   *
   * @param ex the BusinessException
   * @return ResponseEntity containing the error response
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<Response> handleBusinessException(BusinessException ex) {
    Response response =
        Response.builder()
            .success(false)
            .status(ex.getStatus().value())
            .code(ex.getCode())
            .message(i18nService.getMessage(ex.getCode(), ex.getArgs()))
            .data(null)
            .build();
    log.warn(
        "Business Exception Handled: status={}, code={}, message={}",
        response.getStatus(),
        response.getCode(),
        response.getMessage());
    return new ResponseEntity<>(response, ex.getStatus());
  }

  /**
   * Handle all other exceptions and return a generic internal server error response.
   *
   * @param ex the Exception
   * @return ResponseEntity containing the error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Response> handleUnwantedException(Exception ex) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);
    Response response =
        Response.builder()
            .success(false)
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .code(MessageConstant.E_SYS_001)
            .message(i18nService.getMessage(MessageConstant.E_SYS_001))
            .data(null)
            .build();
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Handle access denied exceptions and return a forbidden error response.
   *
   * @param ex the AccessDeniedException or AuthorizationDeniedException
   * @return ResponseEntity containing the error response
   */
  @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
  public ResponseEntity<Response> handleAccessDeniedException(Exception ex) {
    log.warn("Access Denied: {}", ex.getMessage());

    Response response =
        Response.builder()
            .success(false)
            .status(HttpStatus.FORBIDDEN.value())
            .code(MessageConstant.E_SYS_002)
            .message(i18nService.getMessage(MessageConstant.E_SYS_002))
            .data(null)
            .build();

    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
  }
}
