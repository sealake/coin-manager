package net.sealake.coin.auth.token;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * basic auth login token
 */
public class BasicAuthenticationToken extends UsernamePasswordAuthenticationToken {
  public BasicAuthenticationToken(Object principal, Object credentials) {
    super(principal, credentials);
  }

  public BasicAuthenticationToken(Object principal,
      Object credentials,
      Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
  }
}
