package net.sealake.coin.auth;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.auth.token.JwtAuthenticationToken;
import net.sealake.coin.constants.ApiConstants;
import net.sealake.coin.constants.AppError;
import net.sealake.coin.exception.UnauthorizedException;
import net.sealake.coin.util.ServletUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.util.UrlPathHelper;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  public TokenAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
    super(requiresAuthenticationRequestMatcher);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {

    Authentication authentication = null;
    try {
      final String token = getToken(request);
      log.info("当前用户访问的token: {}", token);
      authentication = this.getAuthenticationManager().authenticate(new JwtAuthenticationToken(token));

    } catch (UnauthorizedException ex) {
      log.error("token filter error: {}", ex);
      ServletUtil.outputErrorResponse(ex.getErrorCode(), ex.getMessage(), response, HttpStatus.UNAUTHORIZED);
    }
    return authentication;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      Authentication authResult) throws IOException, ServletException {
    SecurityContextHolder.getContext().setAuthentication(authResult);
    chain.doFilter(request, response);
  }

  /**
   * 从 Authorization header 以及 cookie 中获取 token
   */
  private String getToken(HttpServletRequest request) {
    // token 应放在 Authorization header，且以 "Bearer " 作为前缀
    String token = null;
    String bearerToken = request.getHeader(ApiConstants.AUTH_HEADER);
    if (StringUtils.isNotBlank(bearerToken)) {
      if (StringUtils.startsWithIgnoreCase(bearerToken, ApiConstants.BEARER_TOKEN_PREFIX)) {
        token = StringUtils.substring(token, ApiConstants.BEARER_TOKEN_PREFIX.length());
      } else {
        token = null;
      }
    }

    // 否则从cookie中取 token
    if (StringUtils.isBlank(token)) {
      token = ServletUtil.getCookie(request, ApiConstants.KEY_TOKEN);
    }

    if (StringUtils.isBlank(token)) {
      log.error("request with empty token, request url: {}, bearer token {}",
          new UrlPathHelper().getPathWithinApplication(request), bearerToken);
      throw new UnauthorizedException(AppError.AUTHORIZE_TOKEN_INVALID);
    }

    return token;
  }
}
