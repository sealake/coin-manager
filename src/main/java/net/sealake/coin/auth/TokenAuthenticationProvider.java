package net.sealake.coin.auth;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.auth.token.JwtAuthenticationToken;
import net.sealake.coin.constants.AppError;
import net.sealake.coin.entity.GenericUser;
import net.sealake.coin.exception.UnauthorizedException;
import net.sealake.coin.service.TokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@Slf4j
@Component
public class TokenAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private TokenService tokenService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Assert.notNull(authentication, "No authentication data provided");

    final String token = (String) authentication.getCredentials();

    final GenericUser user = tokenService.parseToken(token);
    if (user != null) {
      final UsernamePasswordAuthenticationToken userToken =
          new UsernamePasswordAuthenticationToken(user.getUsername(), user.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(userToken);
    } else {
      throw new UnauthorizedException(AppError.AUTHORIZE_USER_UNAUTHORIZED);
    }

    JwtAuthenticationToken result = new JwtAuthenticationToken(user, token, user.getAuthorities());
    result.setDetails(authentication.getDetails());
    return result;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return JwtAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
