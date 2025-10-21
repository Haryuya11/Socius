package com.uit.sociuswebfluxapp.exception;

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
import reactor.core.publisher.Mono;

/**
 * GlobalExceptionHandler handles exceptions thrown by controllers in a reactive context and
 * provides appropriate HTTP responses.
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
   * @return Mono containing ResponseEntity with the error response
   */
  @ExceptionHandler(BusinessException.class)
  public Mono<ResponseEntity<Response>> handleBusinessException(BusinessException ex) {
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
        "Reactive Business Exception Handled: status={}, code={}, message={}",
        responseBody.getStatus(),
        responseBody.getCode(),
        responseBody.getMessage());

    return Mono.just(new ResponseEntity<>(responseBody, ex.getStatus()));
  }

  /**
   * Handle all other exceptions and return a generic internal server error response.
   *
   * @param ex the Exception
   * @return Mono containing ResponseEntity with the error response
   */
  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<Response>> handleUnwantedException(Exception ex) {
    log.error("An unexpected internal server error occurred in reactive context", ex);

    Response responseBody =
        Response.builder()
            .success(false)
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .code(MessageConstant.UNEXPECTED_ERROR)
            .message(i18nService.getMessage(MessageConstant.UNEXPECTED_ERROR))
            .data(null)
            .build();

    return Mono.just(new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR));
  }
}
