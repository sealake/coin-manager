package net.sealake.coin.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.sealake.coin.entity.enums.RoleTypeEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
  private String username;
  private String password;
  private RoleTypeEnum role = RoleTypeEnum.ROLE_USER;


}