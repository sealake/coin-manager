package net.sealake.coin.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.api.request.AuthRequest;
import net.sealake.coin.configuration.Settings;
import net.sealake.coin.exception.BadRequestException;
import net.sealake.coin.exception.UnauthorizedException;
import net.sealake.coin.util.ServletUtil;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class BasicAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  private Settings settings;
  private BasicAuthenticationSuccessHandler loginSuccessHandler;

  public BasicAuthenticationFilter(AntPathRequestMatcher authMatcher, Settings settings,
      BasicAuthenticationSuccessHandler loginSuccessHandler) {
    super(authMatcher);
    this.settings = settings;
    this.loginSuccessHandler = loginSuccessHandler;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {

    UsernamePasswordAuthenticationToken basicToken = null;
    try {
      final String payload = ServletUtil.getBody(request);
      AuthRequest authRequest = new ObjectMapper().readValue(payload, AuthRequest.class);
      basicToken = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
      log.info("用户正在进行登录操作, 登录用户 {}", authRequest.getUsername());

      return this.getAuthenticationManager().authenticate(basicToken);

    } catch (BadRequestException ex) {
      log.error("login Authentication error: {}:", ex);
      ServletUtil.outputErrorResponse(ex.getErrorCode(), ex.getMessage(), response, HttpStatus.BAD_REQUEST);
    } catch (UnauthorizedException ex) {
      log.error("login Authentication error: {}:", ex);
      ServletUtil.outputErrorResponse(ex.getErrorCode(), ex.getMessage(), response, HttpStatus.UNAUTHORIZED);
    }
    return null;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {
    loginSuccessHandler.onAuthenticationSuccess(request, response, authResult);
  }

}
