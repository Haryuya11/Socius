package com.uit.sociuswebfluxapp.security;

import com.uit.sociuscoremodules.shared.security.TokenAuthenticator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * ReactiveAuthorizationFilter is a WebFilter that intercepts HTTP requests to perform token-based
 * authentication and set the security context in a reactive environment.
 */
@Component
@RequiredArgsConstructor
public class ReactiveAuthorizationFilter implements WebFilter {

  /** TokenAuthenticator for validating JWT tokens. */
  private final TokenAuthenticator tokenAuthenticator;

  /**
   * Filters incoming HTTP requests to authenticate using a Bearer token.
   *
   * @param exchange the current server exchange
   * @param chain provides a way to delegate to the next filter
   * @return a Mono that indicates when request processing is complete
   */
  @NonNull
  @Override
  public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return chain.filter(exchange);
    }

    String token = authHeader.substring(7);
    try {
      Authentication authentication = tokenAuthenticator.authenticate(token);
      return chain
          .filter(exchange)
          .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    } catch (Exception e) {
      return Mono.error(e);
    }
  }
}
