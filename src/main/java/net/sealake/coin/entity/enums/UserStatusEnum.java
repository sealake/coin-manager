package net.sealake.coin.entity.enums;

public enum UserStatusEnum {
  ACTIVE,
  INACTIVE
  ;

  public static boolean isActive(UserStatusEnum statusEnum) {
    return ACTIVE.equals(statusEnum);
  }
}