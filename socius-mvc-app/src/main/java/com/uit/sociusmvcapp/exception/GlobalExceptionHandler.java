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
    String code = ex.getCode();
    String message = i18nService.getMessage(code, ex.getArgs());

    Response responseBody =
        Response.builder()
            .success(false)
            .status(ex.getStatus().value())
            .code(code)
            .message(message)
            .data(null)
            .build();

    log.warn(
        "Business Exception Handled: status={}, code={}, message={}",
        responseBody.getStatus(),
        responseBody.getCode(),
        responseBody.getMessage());
    return new ResponseEntity<>(responseBody, ex.getStatus());
  }

  /**
   * Handle all other exceptions and return a generic internal server error response.
   *
   * @param ex the Exception
   * @return ResponseEntity containing the error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Response> handleUnwantedException(Exception ex) {
    log.error("An unexpected internal server error occurred", ex);

    String errorCode = MessageConstant.UNEXPECTED_ERROR;
    String errorMessage = i18nService.getMessage(errorCode);

    Response responseBody =
        Response.builder()
            .success(false)
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .code(errorCode)
            .message(errorMessage)
            .data(null)
            .build();

    return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
