package com.uit.sociuswebfluxapp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/** Test security configuration that permits all requests for testing purposes. */
@TestConfiguration
@EnableWebFluxSecurity
public class TestSecurityConfig {

  /**
   * Configures a permissive security filter chain for tests.
   *
   * @param http the ServerHttpSecurity to configure
   * @return the configured SecurityWebFilterChain
   */
  @Bean
  public SecurityWebFilterChain testSecurityFilterChain(ServerHttpSecurity http) {
    http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(exchange -> exchange.anyExchange().permitAll());
    return http.build();
  }
}
