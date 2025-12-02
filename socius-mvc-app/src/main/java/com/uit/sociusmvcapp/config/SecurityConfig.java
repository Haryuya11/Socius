package com.uit.sociusmvcapp.config;

import com.uit.sociuscoremodules.shared.constants.SecurityConstant;
import com.uit.sociuscoremodules.shared.security.TokenAuthenticator;
import com.uit.sociusmvcapp.security.AuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * SecurityConfig configures the security settings for the application, including CORS and
 * authorization filters.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  /** TokenAuthenticator for validating JWT tokens. */
  private final TokenAuthenticator tokenAuthenticator;

  /**
   * Configures the security filter chain.
   *
   * @param http the HttpSecurity object
   * @return the configured SecurityFilterChain
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    AuthorizationFilter authorizationFilter = new AuthorizationFilter(tokenAuthenticator);
    http.cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authz ->
                authz
                    .requestMatchers(HttpMethod.OPTIONS, SecurityConstant.ALL_PATHS)
                    .permitAll()
                    .requestMatchers(SecurityConstant.PUBLIC_ENDPOINTS.toArray(String[]::new))
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * Configures the CORS settings for the application.
   *
   * @return the CorsConfigurationSource
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(SecurityConstant.CORS_ALLOWED_ORIGIN_PATTERNS);
    configuration.setAllowedMethods(SecurityConstant.CORS_ALLOWED_METHODS);
    configuration.setAllowedHeaders(SecurityConstant.CORS_ALLOWED_HEADERS);
    configuration.setAllowCredentials(SecurityConstant.CORS_ALLOW_CREDENTIALS);
    configuration.setMaxAge(SecurityConstant.MAX_AGE);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
