package com.uit.sociusmvcapp.iam.internal.config;

import com.uit.sociusmvcapp.iam.internal.component.JwtAuthenticationConverter;
import com.uit.sociusmvcapp.shared.constants.SecurityConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
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
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationConverter jwtAuthenticationConverter;

  @Value("${azure.graph.jwk-set-uri}")
  private String jwkSetUri;

  @Value("${azure.graph.issuer-uri}")
  private String issuerUri;

  /**
   * Configures the security filter chain.
   *
   * @param http the HttpSecurity object
   * @return the configured SecurityFilterChain
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
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
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(
                    jwt ->
                        jwt.decoder(jwtDecoder())
                            .jwtAuthenticationConverter(jwtAuthenticationConverter)));

    return http.build();
  }

  /**
   * Creates and configures a {@link JwtDecoder} for decoding and validating JWTs.
   *
   * <p>This decoder uses the RS256 algorithm and retrieves public keys from the provided JWK Set
   * URI ({@code jwkSetUri}). It also applies default JWT validation with the specified issuer
   * ({@code issuerUri}) to ensure the token's authenticity and integrity.
   *
   * @return a configured {@link JwtDecoder} ready to decode and validate JWTs
   */
  @Bean
  public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder jwtDecoder =
        NimbusJwtDecoder.withJwkSetUri(jwkSetUri).jwsAlgorithm(SignatureAlgorithm.RS256).build();
    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
    jwtDecoder.setJwtValidator(withIssuer);

    return jwtDecoder;
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
