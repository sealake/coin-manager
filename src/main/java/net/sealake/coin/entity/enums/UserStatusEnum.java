package net.sealake.coin.entity.enums;

import org.apache.commons.lang3.StringUtils;

public enum UserStatusEnum {
  ACTIVE,
  INACTIVE
  ;

  public static UserStatusEnum getByCode(final String statusCode) {
    if (StringUtils.isBlank(statusCode)) {
      return null;
    }

    for (UserStatusEnum status: UserStatusEnum.values()) {
      if (StringUtils.equalsIgnoreCase(status.name(), statusCode)) {
        return status;
      }
    }

    return null;
  }

  public static boolean isActive(UserStatusEnum statusEnum) {
    return statusEnum != null && ACTIVE.equals(statusEnum);
  }
}