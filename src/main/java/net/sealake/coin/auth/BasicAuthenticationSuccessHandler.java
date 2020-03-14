package net.sealake.coin.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.api.response.AuthResponse;
import net.sealake.coin.constants.ApiConstants;
import net.sealake.coin.entity.GenericUser;
import net.sealake.coin.service.TokenService;
import net.sealake.coin.util.ServletUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class BasicAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  @Autowired
  private TokenService tokenService;

  @Override
  public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    //获得授权后可得到用户信息   可使用UserService进行数据库操作
    final GenericUser user = (GenericUser) authentication.getPrincipal();
    SecurityContextHolder.getContext().setAuthentication(authentication);

    //输出登录提示信息
    log.info("用户{}成功登录！", user.getUsername());

    // 设置token信息到cookie和httpResponse
    final String token = tokenService.generateToken(user);
    ServletUtil.saveCookie(request, response, ApiConstants.KEY_TOKEN, token);
    AuthResponse authResponse = AuthResponse.builder().token(token).build();
    response.getWriter().write(new ObjectMapper().writeValueAsString(authResponse));
    response.getWriter().flush();
  }

  /**
   * 更新cookie.
   */
  public void updateCookieToken(final String token, final HttpServletRequest request, final HttpServletResponse
      response) {
    final Cookie[] cookies = request.getCookies();

    boolean addCookieFlag = false;
    //如果请求中已经有了，则进行更新
    if (cookies != null) {
      for (Cookie c : cookies) {
        if (StringUtils.equals(c.getName(), "token")) {
          c.setValue(token);
          response.addCookie(c);
          addCookieFlag = true;
        }
      }
    }
    //如果请求中没有找到token记录，则新建一个
    if (!addCookieFlag) {
      final Cookie myCookie = new Cookie("token", token);
      response.addCookie(myCookie);
    }
  }
}
