package com.uit.sociuswebfluxapp.config;

import com.uit.sociuscoremodules.shared.constants.SecurityConstant;
import com.uit.sociuswebfluxapp.constants.WebSocketConstant;
import com.uit.sociuswebfluxapp.security.ReactiveAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * SecurityConfig configures security settings for the web application, including authentication and
 * authorization rules.
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  /** ReactiveAuthorizationFilter for handling token-based authentication. */
  private final ReactiveAuthorizationFilter reactiveAuthorizationFilter;

  /**
   * Configures the security web filter chain.
   *
   * @param http the ServerHttpSecurity to configure
   * @return the configured SecurityWebFilterChain
   */
  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .cors(ServerHttpSecurity.CorsSpec::disable)
        .addFilterAt(reactiveAuthorizationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        .authorizeExchange(
            ex ->
                ex.pathMatchers(SecurityConstant.PUBLIC_ENDPOINTS.toArray(String[]::new))
                    .permitAll()
                    .pathMatchers(WebSocketConstant.WEBSOCKET_WILDCARD_PATH)
                    .permitAll()
                    .anyExchange()
                    .authenticated())
        .build();
  }
}
