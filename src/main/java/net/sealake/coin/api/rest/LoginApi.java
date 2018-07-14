/**
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */
package net.sealake.coin.api.rest;

import io.swagger.annotations.ApiOperation;

import net.sealake.coin.api.request.AuthRequest;
import net.sealake.coin.api.response.AuthResponse;
import net.sealake.coin.constants.ApiConstants;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author melody
 * @version $Id: LoginApi.java, v 0.1 2018年07月13日 下午10:39 melody Exp $
 */
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