package com.uit.sociusmvcapp.iam.internal.component;

import com.uit.sociusmvcapp.iam.AuthorizationService;
import com.uit.sociusmvcapp.iam.dto.UserPrincipal;
import com.uit.sociusmvcapp.shared.constants.SecurityConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Converter that extracts user information from JWT and creates Authentication with UserPrincipal.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

  private final AuthorizationService authorizationService;

  @Override
  public AbstractAuthenticationToken convert(@NotNull Jwt jwt) {
    try {
      // Extract clientId from JWT's "oid" claim
      String clientId = jwt.getClaimAsString(SecurityConstant.OID_CLAIM_NAME);

      if (clientId == null || clientId.isEmpty()) {
        log.warn("JWT missing 'oid' claim");
        return null;
      }

      // Authorize user and get full principal with permissions
      UserPrincipal userPrincipal = authorizationService.authorize(clientId);

      return new UsernamePasswordAuthenticationToken(
          userPrincipal, null, userPrincipal.getAuthorities());
    } catch (Exception e) {
      log.error("Failed to convert JWT to Authentication: {}", e.getMessage(), e);
      return null;
    }
  }
}
