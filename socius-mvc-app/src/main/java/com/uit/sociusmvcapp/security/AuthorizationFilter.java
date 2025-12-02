package com.uit.sociusmvcapp.security;

import com.uit.sociuscoremodules.shared.constants.SecurityConstant;
import com.uit.sociuscoremodules.shared.security.TokenAuthenticator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * AuthorizationFilter is a servlet filter that intercepts HTTP requests to perform token-based
 * authentication using the TokenAuthenticator. It checks for the presence of a Bearer token in the
 * Authorization header, validates it, and sets the authentication in the security context.
 */
@Slf4j
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

  /** TokenAuthenticator for validating JWT tokens. */
  private final TokenAuthenticator tokenAuthenticator;

  /**
   * Filters incoming HTTP requests to perform token-based authentication.
   *
   * @param request the HTTP request
   * @param response the HTTP response
   * @param filterChain the filter chain
   * @throws ServletException if a servlet error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String requestUri = request.getRequestURI();

    // Skip filter for public endpoints
    if (requestUri.equals("/health")
        || requestUri.equals("/ping")
        || requestUri.equals("/favicon.ico")
        || requestUri.equals("/robots.txt")
        || requestUri.equals("/error")) {
      filterChain.doFilter(request, response);
      return;
    }

    String authHeader = request.getHeader(SecurityConstant.AUTHORIZATION_HEADER);

    if (authHeader == null || !authHeader.startsWith(SecurityConstant.BEARER_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(SecurityConstant.BEARER_PREFIX_LENGTH);
    try {
      Authentication authentication = tokenAuthenticator.authenticate(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (Exception e) {
      log.error("Authentication failed: {}", e.getMessage());
      SecurityContextHolder.clearContext();
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
      return;
    }

    filterChain.doFilter(request, response);
  }
}
