package net.sealake.coin.configuration;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Settings {
  @Value("${user.admin.username}")
  private String adminUserName;

  @Value("${user.admin.password}")
  private String adminPassword;

  @Value("${jwt.token.expires}")
  private int tokenExpireSeconds;

  @Value("${jwt.token.secret}")
  private String tokenSecret;

  @Value("${jwt.token.issuer}")
  private String tokenIssuer;
}