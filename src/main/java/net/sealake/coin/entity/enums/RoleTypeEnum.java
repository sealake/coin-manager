package net.sealake.coin.entity.enums;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

@Getter
public enum RoleTypeEnum {

  ROLE_ADMIN("ROLE_ADMIN","管理员角色"),
  ROLE_USER("ROLE_USER","普通用户角色"),
  ;

  private String code;
  private String description;

  private RoleTypeEnum(final String code, final String description) {
    this.code = code;
    this.description = description;
  }

  private RoleTypeEnum getByCode(final String code) {
    for (RoleTypeEnum roleTypeEnum : RoleTypeEnum.values()) {
      if (StringUtils.equals(roleTypeEnum.getCode(), code)) {
        return roleTypeEnum;
      }
    }

    return null;
  }
}