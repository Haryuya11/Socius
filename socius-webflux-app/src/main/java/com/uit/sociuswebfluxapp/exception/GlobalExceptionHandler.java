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
    Response response =
        Response.builder()
            .success(false)
            .status(ex.getStatus().value())
            .code(ex.getCode())
            .message(i18nService.getMessage(ex.getCode(), ex.getArgs()))
            .data(null)
            .build();
    log.warn(
        "Reactive Business Exception Handled: status={}, code={}, message={}",
        response.getStatus(),
        response.getCode(),
        response.getMessage());
    return Mono.just(new ResponseEntity<>(response, ex.getStatus()));
  }

  /**
   * Handle all other exceptions and return a generic internal server error response.
   *
   * @param ex the Exception
   * @return Mono containing ResponseEntity with the error response
   */
  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<Response>> handleUnwantedException(Exception ex) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);
    Response responseBody =
        Response.builder()
            .success(false)
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .code(MessageConstant.E_SYS_001)
            .message(i18nService.getMessage(MessageConstant.E_SYS_001))
            .data(null)
            .build();
    return Mono.just(new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR));
  }
}
