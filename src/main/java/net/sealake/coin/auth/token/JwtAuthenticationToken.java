package net.sealake.coin.auth.token;

import lombok.Getter;

import net.sealake.coin.constants.AppError;
import net.sealake.coin.exception.UnauthorizedException;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Collection;

/**
 * jwt authentication token
 */
@Getter
public class JwtAuthenticationToken extends PreAuthenticatedAuthenticationToken {
  private final Object principal;
  private Object credentials;

  public JwtAuthenticationToken(Object credentials) {
    super(null, credentials);
    this.principal = null;
    this.credentials = credentials;
  }

  public JwtAuthenticationToken(Object principal, Object credentials,
      Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
    this.principal = principal;
    this.credentials = credentials;
    super.setAuthenticated(true); // must use super, as we override
  }

  @Override
  public void eraseCredentials() {
    super.eraseCredentials();
    credentials = null;
  }
}
