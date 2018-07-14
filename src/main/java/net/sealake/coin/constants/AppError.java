package net.sealake.coin.constants;

public enum AppError {

  AUTHORIZE_BAD_CREDENTIALS(4400, "user.name.or.password.error"), //
  AUTHORIZE_USER_UNAUTHORIZED(4401, "user.unauthorized"), //
  AUTHORIZE_TOKEN_INVALID(4402, "authorize.token.invalid"),         //
  PERMISSION_DENIED(4403, "authorize.permission.denied"), //
  DOCUMENT_NOT_FOUND(4004, "document.not.fountd"), //

  OTHER_METHOD_ARGS_NOT_VALID(9000, ""), //
  OTHER_HTTP_MEDIATYPE_NOT_SUPPORT(9001, "other.contenttype.unsupport"), //
  OTHER_HTTP_MESSAGE_NOT_READABLE(9002, "other.message.not.readable"), //
  OTHER_HTTP_TYPE_MISMATCH(9003, "other.type.mismatch"), //
  OTHER_SERVER_INERNAL_EXCEPTION(9999, "other.server.internal.error") //
  ;

  private int errorCode;
  private String messageKey;

  AppError(final int code, final String messageKey) {
    this.errorCode = code;
    this.messageKey = messageKey;
  }

  public int getErrorCode() {
    return this.errorCode;
  }

  public String getMessageKey() {
    return this.messageKey;
  }

  public static AppError of(final int errorCode) {
    AppError appError = null;
    for (final AppError error : values()) {
      if (error.getErrorCode() == errorCode) {
        appError = error;
      }
    }

    return appError;
  }
}
