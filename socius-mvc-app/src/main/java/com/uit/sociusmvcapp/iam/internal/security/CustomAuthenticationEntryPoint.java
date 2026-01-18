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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Custom AuthenticationEntryPoint that returns a JSON response for 401 Unauthorized errors. This
 * handler is used by Spring Security when authentication is required but not provided.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final I18nService i18nService;
  private final ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    log.warn(
        "Authentication required for [{} {}]: {}",
        request.getMethod(),
        request.getRequestURI(),
        authException.getMessage());

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    Response errorResponse =
        Response.builder()
            .success(false)
            .status(HttpStatus.UNAUTHORIZED.value())
            .code(MessageConstant.E_SYS_003)
            .message(i18nService.getMessage(MessageConstant.E_SYS_003))
            .data(null)
            .build();

    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
