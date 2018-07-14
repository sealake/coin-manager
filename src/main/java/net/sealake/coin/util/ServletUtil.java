package net.sealake.coin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.constants.AppError;
import net.sealake.coin.exception.BadRequestException;
import net.sealake.coin.exception.ErrorMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class ServletUtil {

  /**
   * 写入或复写cookie值
   * @param request
   */
  public static void saveCookie(final HttpServletRequest request, final HttpServletResponse
      response, final String key, final String value) {
    if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
      return;
    }

    // final Cookie myCookie = new Cookie(key, value);
    // response.addCookie(myCookie);

    boolean addCookieFlag = false;
    final Cookie[] cookies = request.getCookies();
    //如果请求中已经有了，则进行更新
    if (cookies != null) {
      for (Cookie c : cookies) {
        if (StringUtils.equalsIgnoreCase(c.getName(), key)) {
          c.setValue(value);
          response.addCookie(c);
          addCookieFlag = true;
        }
      }
    }

    //如果请求中没有找到token记录，则新建一个
    if (!addCookieFlag) {
      final Cookie myCookie = new Cookie(key, value);
      response.addCookie(myCookie);
    }
  }

  /**
   * 根据名字从cookie中读取值
   * @param request
   * @param name
   * @return
   */
  public static String getCookie(final HttpServletRequest request, final String name) {
    if (StringUtils.isBlank(name)) {
      return null;
    }

    final Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }

    for (Cookie cookie : cookies) {
      if (StringUtils.equalsIgnoreCase(cookie.getName(), name)) {
        return cookie.getValue();
      }

    }

    return null;
  }

  /**
   * 获取HttpBody的值
   * @param request
   * @return
   */
  public static String getBody(final HttpServletRequest request) {
    InputStream input = null;
    final String body;
    try {
      request.setCharacterEncoding("UTF-8");
      // 获取content-length
      final int contentLength = request.getContentLength();
      if (contentLength <= 0) {
        log.error("error contentLength is zero, username and password empty!");
        throw new BadRequestException(AppError.AUTHORIZE_BAD_CREDENTIALS);
      }

      input = request.getInputStream();
      final byte[] bodyInBytes = new byte[contentLength];

      // 读取body内容
      int readLen = 0;
      int ret = 0;
      while (readLen != contentLength) {
        ret = input.read(bodyInBytes, readLen, contentLength - readLen);
        if (ret == -1) {
          break;
        }
        readLen += ret;
      }

      // 数据被截断
      if (readLen != contentLength) {
        log.error("data interrupt, readLen: {}, contentLength: {}", readLen, contentLength);
        throw new BadRequestException(AppError.AUTHORIZE_BAD_CREDENTIALS);
      }

      body = new String(bodyInBytes);

    } catch (IOException ex) {
      log.error("getBody error: ", ex);
      throw new BadRequestException(AppError.AUTHORIZE_BAD_CREDENTIALS);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException ex) {
          log.error("close inputstream error.", ex);
        }
      }
    }
    return body;
  }

  // 如下方法将error返回值写入httpResponse
  public static void outputErrorResponse(final HttpServletResponse httpResponse, final HttpStatus status) {
    AppError appError = AppError.OTHER_SERVER_INERNAL_EXCEPTION;
    switch (status) {
      case UNAUTHORIZED:
        appError = AppError.AUTHORIZE_USER_UNAUTHORIZED;
        break;
      case FORBIDDEN:
        appError = AppError.PERMISSION_DENIED;
        break;
      default:
        break;
    }
    outputErrorResponse(appError.getErrorCode(), appError.getMessageKey(), httpResponse, status);
  }

  public static void outputErrorResponse(final AppError appError,
      final HttpStatus status, final HttpServletResponse httpResponse) {
    outputErrorResponse(appError.getErrorCode(), appError.getMessageKey(), httpResponse, status);
  }

  public static void outputErrorResponse(final int errorCode, //
      final String message, //
      final HttpServletResponse httpResponse, //
      final HttpStatus status) {

    final ErrorMessage errorMessage = new ErrorMessage(errorCode, message);

    try {
      final String tokenJsonResponse = new ObjectMapper().writeValueAsString(errorMessage);
      httpResponse.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
      httpResponse.setStatus(status.value());
      httpResponse.getWriter().print(tokenJsonResponse);
    } catch (Exception ex) {
      log.error("failed output error info to httpResponse, exception: {}", ex);
    }
  }
}
