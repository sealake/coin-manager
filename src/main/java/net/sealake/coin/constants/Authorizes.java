package net.sealake.coin.constants;

import net.sealake.coin.entity.enums.RoleTypeEnum;

public class Authorizes {

  public static final String ROLE_ADMIN = "ROLE_ADMIN";
  public static final String ROLE_USER = "ROLE_USER";

  public static final String ALL_ROLES = "hasAnyRole('" + ROLE_ADMIN + "','" + ROLE_USER + "')";
  public static final String ADMIN_USER = "hasAnyRole('" + ROLE_ADMIN + "','" + ROLE_USER + "')";

  public static final String ADMIN = "hasAnyRole('" + ROLE_ADMIN + "')";
  public static final String USER = "hasAnyRole('" + ROLE_USER + "')";
}
