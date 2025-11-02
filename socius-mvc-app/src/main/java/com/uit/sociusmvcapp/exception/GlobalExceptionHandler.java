package com.uit.sociusmvcapp.exception;

import com.uit.sociuscoremodules.shared.constants.MessageConstant;
import com.uit.sociuscoremodules.shared.exception.BusinessException;
import com.uit.sociuscoremodules.shared.response.Response;
import com.uit.sociuscoremodules.shared.service.I18nService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
