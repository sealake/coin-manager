package net.sealake.coin.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录参数
 * @author melody
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
  /** 用户名 */
  private String username;
  /** 密码 */
  private String password;
}