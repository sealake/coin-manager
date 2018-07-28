package net.sealake.coin.constants;

public class ApiConstants {
  public static final String API = "/api";
  public static final String V1 = "v1";
  public static final String PATH_SEPERATOR = "/";

  public static final String API_V1 = API + PATH_SEPERATOR + V1;

  // login url
  public static final String LOGIN_URL = "/login";
  public static final String API_V1_LOGIN = API_V1 + LOGIN_URL;

  // http properties
  public static final String AUTH_HEADER = "Authorization";
  public static final String BEARER_TOKEN_PREFIX = "Bearer ";
  public static final String KEY_TOKEN = "token";

  // 字符串连接 分隔符
  public static final String SEPERATOR_DOUBLE_UNDERLINE = "__";
  public static final String SEPERATOR_UNDERLINE = "_";
  public static final String SEPERATOR_MINUS_SIGN = "-";
  public static final String SEPERATOR_SLASH = "/";
  public static final String SEPERATOR_EMPTY = "";
}