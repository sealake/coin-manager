package net.sealake.coin.service.integration.entity.enums;

public enum CoinOrderStatusEnum {
  NEW,
  PARTIALLY_FILLED,
  FILLED,
  CANCELED,
  PENDING_CANCEL,
  REJECTED,
  EXPIRED;

  public static boolean isFinish(CoinOrderStatusEnum status) {
    return FILLED.equals(status) || CANCELED.equals(status) || REJECTED.equals(status) || EXPIRED.equals(status);
  }

  public static boolean isSuccess(CoinOrderStatusEnum status) {
    return FILLED.equals(status);
  }

  public static boolean isFail(CoinOrderStatusEnum status) {
    return CANCELED.equals(status) || REJECTED.equals(status) || EXPIRED.equals(status);
  }

  public static boolean isProcessing(CoinOrderStatusEnum status) {
    return PARTIALLY_FILLED.equals(status) || PENDING_CANCEL.equals(status);
  }

  public static boolean isInit(CoinOrderStatusEnum status) {
    return NEW.equals(status);
  }
}