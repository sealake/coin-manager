package net.sealake.coin.api.rest;

import com.jcabi.aspects.Loggable;

import io.swagger.annotations.ApiOperation;

import net.sealake.coin.api.request.AuthRequest;
import net.sealake.coin.api.response.AuthResponse;
import net.sealake.coin.constants.ApiConstants;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户登录api
 */
@Loggable
@RestController
@RequestMapping(ApiConstants.API_V1)
public class LoginApi {
  @PostMapping("/login")
  @ApiOperation(response = AuthResponse.class, value = "登录接口")
  public AuthResponse login(@RequestBody AuthRequest authRequest) {
    // 只是表现层接口，真正的登录逻辑在BasicAuthenticationFileter中执行
    return null;
  }
}