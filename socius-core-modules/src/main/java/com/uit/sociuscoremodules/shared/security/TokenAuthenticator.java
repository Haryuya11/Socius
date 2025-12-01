package com.uit.sociuscoremodules.shared.security;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.uit.sociuscoremodules.employee.dto.EmployeeDto;
import com.uit.sociuscoremodules.shared.constants.SecurityConstant;
import com.uit.sociuscoremodules.shared.exception.BusinessException;
import com.uit.sociuscoremodules.shared.service.AuthorizationService;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * TokenAuthenticator is responsible for validating JWT tokens issued by Azure AD. It verifies the
 * token's signature and claims, and retrieves user information to create an Authentication object.
 */
@Component
@Slf4j
public class TokenAuthenticator {
  /** Azure AD tenant ID. */
  private final String tenantId;

  /** Azure AD client ID. */
  private final String clientId;

  /** Service for user authorization. */
  private final AuthorizationService authorizationService;

  /** JWK Source for token validation. */
  private final JWKSource<SecurityContext> jwkSource;

  /**
   * Constructor for TokenAuthenticator.
   *
   * <p>Initializes the TokenAuthenticator with the necessary services and configuration values.
   *
   * @param authorizationService authorization service to fetch user details
   * @param tenantId Azure AD tenant ID
   * @param clientId Azure AD client ID
   */
  public TokenAuthenticator(
      AuthorizationService authorizationService,
      @Value("${spring.cloud.azure.active-directory.profile.tenant-id}") String tenantId,
      @Value("${spring.cloud.azure.active-directory.credential.client-id}") String clientId) {
    this.authorizationService = authorizationService;
    this.tenantId = tenantId;
    this.clientId = clientId;
    this.jwkSource = createJwkSource();
  }

  /**
   * Create JWK source using the builder pattern (non-deprecated approach).
   *
   * @return JWKSource instance
   */
  private JWKSource<SecurityContext> createJwkSource() {
    try {
      String uri = String.format(SecurityConstant.JSON_WEB_KEY_SET_URL, tenantId, tenantId);
      ResourceRetriever resourceRetriever =
          new DefaultResourceRetriever(
              SecurityConstant.JWK_CONNECT_TIMEOUT_MILLIS,
              SecurityConstant.JWK_READ_TIMEOUT_MILLIS,
              SecurityConstant.JWK_SIZE_LIMIT_BYTES);

      long timeToLiveMillis = TimeUnit.DAYS.toMillis(SecurityConstant.CACHE_TIME_TO_LIVE_DAYS);
      long cacheRefreshTimeoutMillis =
          TimeUnit.HOURS.toMillis(SecurityConstant.CACHE_REFRESH_TIMEOUT_HOURS);

      return JWKSourceBuilder.create(new URL(uri), resourceRetriever)
          .cache(timeToLiveMillis, cacheRefreshTimeoutMillis)
          .rateLimited(true)
          .build();
    } catch (Exception e) {
      log.error("Failed to create JWK source", e);
      throw new IllegalStateException("Failed to initialize JWK source", e);
    }
  }

  /**
   * Authenticate the provided JWT token.
   *
   * @param token the JWT token as a String
   * @return Authentication object if the token is valid
   * @throws Exception if token parsing or validation fails
   */
  public Authentication authenticate(String token) throws Exception {
    SignedJWT signedJwt = SignedJWT.parse(token);

    if (!validateSignature(signedJwt)) {
      throw new BadCredentialsException("Invalid token signature");
    }

    JWTClaimsSet claims = signedJwt.getJWTClaimsSet();

    if (!validateClaims(claims)) {
      throw new BadCredentialsException("Invalid token claims");
    }

    String userId = claims.getClaim(SecurityConstant.OID_CLAIM_NAME).toString();
    EmployeeDto user = authorizationService.authorize(userId);

    if (user == null) {
      throw new UsernameNotFoundException("User not found in system for oid: " + userId);
    }

    List<SimpleGrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority(SecurityConstant.DEFAULT_ROLE_USER));

    return new UsernamePasswordAuthenticationToken(user, null, authorities);
  }

  /**
   * Validate the signature of the JWT token.
   *
   * @param signedJwt the SignedJWT object
   * @return true if the signature is valid, false otherwise
   * @throws Exception if signature verification fails
   */
  private boolean validateSignature(SignedJWT signedJwt) throws Exception {
    String kid = signedJwt.getHeader().getKeyID();
    List<JWK> jwks = null;
    Exception lastException = null;

    try {
      JWKSelector jwkSelector = new JWKSelector(new JWKMatcher.Builder().keyID(kid).build());
      jwks = jwkSource.get(jwkSelector, null);
    } catch (Exception e) {
      lastException = e;
      log.warn("Failed to retrieve JWKs for kid {}: {}", kid, e.getMessage());
    }

    if (jwks == null || jwks.isEmpty()) {
      log.error("Public keys not found for kid: {}", kid);
      if (lastException != null) {
        log.error("Last exception: ", lastException);
      }
      return false;
    }

    JWK jwk = jwks.get(0);
    RSAPublicKey publicKey = jwk.toRSAKey().toRSAPublicKey();
    JWSVerifier verifier = new RSASSAVerifier(publicKey);

    return signedJwt.verify(verifier);
  }

  /**
   * Validate the claims of the JWT token.
   *
   * @param claims the JWTClaimsSet object
   * @return true if the claims are valid, false otherwise
   */
  private boolean validateClaims(JWTClaimsSet claims) {
    String expectedIssuer = String.format(SecurityConstant.ISSUER_URI_FORMAT, tenantId, tenantId);

    if (!expectedIssuer.equals(claims.getIssuer())) {
      log.warn("Invalid issuer: expected {}, got {}", expectedIssuer, claims.getIssuer());
      return false;
    }

    List<String> audiences = claims.getAudience();
    if (audiences == null || !audiences.contains(clientId)) {
      log.warn("Invalid audience: expected {}, got {}", clientId, audiences);
      return false;
    }

    Date expiration = claims.getExpirationTime();
    if (expiration == null || expiration.before(new Date())) {
      log.warn("Token expired: {}", expiration);
      throw new BusinessException(HttpStatus.UNAUTHORIZED, "TOKEN_EX");
    }

    String subject = claims.getSubject();
    if (subject == null || subject.isEmpty()) {
      log.warn("Missing or empty subject claim");
      return false;
    }

    return true;
  }
}
