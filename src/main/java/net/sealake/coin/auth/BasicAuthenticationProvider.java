package net.sealake.coin.auth;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.auth.token.BasicAuthenticationToken;
import net.sealake.coin.constants.AppError;
import net.sealake.coin.entity.GenericUser;
import net.sealake.coin.exception.BadRequestException;
import net.sealake.coin.exception.UnauthorizedException;
import net.sealake.coin.service.GenericUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class BasicAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private GenericUserService userService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Optional<String> username = Optional.ofNullable(authentication.getPrincipal().toString());
    Optional<String> password = Optional.ofNullable(authentication.getCredentials().toString());

    if (credentialsMissing(username, password)) {
      throw new BadRequestException(AppError.AUTHORIZE_BAD_CREDENTIALS);
    }

    GenericUser user = userService.checkUser(username.get(), password.get());
    if (null == user) {
      throw new BadRequestException(AppError.AUTHORIZE_BAD_CREDENTIALS);
    }

    return new BasicAuthenticationToken(user, null, user.getAuthorities());
  }

  private boolean credentialsMissing(Optional<String> username, Optional<String> password) {
    return !username.isPresent() || !password.isPresent();
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
