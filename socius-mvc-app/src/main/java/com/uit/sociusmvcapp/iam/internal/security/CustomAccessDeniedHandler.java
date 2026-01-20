package com.uit.sociusmvcapp.iam.internal.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uit.sociusmvcapp.shared.constants.MessageConstant;
import com.uit.sociusmvcapp.shared.response.Response;
import com.uit.sociusmvcapp.shared.service.I18nService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Custom AccessDeniedHandler that returns a JSON response for 403 Forbidden errors. This handler is
 * used by Spring Security when access is denied due to insufficient permissions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final I18nService i18nService;
  private final ObjectMapper objectMapper;

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {

    log.warn(
        "Access denied for [{} {}]: {}",
        request.getMethod(),
        request.getRequestURI(),
        accessDeniedException.getMessage());

    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    Response errorResponse =
        Response.builder()
            .success(false)
            .status(HttpStatus.FORBIDDEN.value())
            .code(MessageConstant.E_SYS_002)
            .message(i18nService.getMessage(MessageConstant.E_SYS_002))
            .data(null)
            .build();

    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
