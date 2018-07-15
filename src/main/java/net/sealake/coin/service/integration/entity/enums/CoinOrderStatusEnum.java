package net.sealake.coin.service.integration.entity.enums;

public enum CoinOrderStatusEnum {
  NEW,
  PARTIALLY_FILLED,
  FILLED,
  CANCELED,
  PENDING_CANCEL,
  REJECTED,
  EXPIRED;

  public boolean isFinish(CoinOrderStatusEnum status) {
    return FILLED.equals(status) || CANCELED.equals(status) || REJECTED.equals(status) || EXPIRED.equals(status);
  }

  public boolean isSuccess(CoinOrderStatusEnum status) {
    return FILLED.equals(status);
  }

  public boolean isFail(CoinOrderStatusEnum status) {
    return CANCELED.equals(status) || REJECTED.equals(status) || EXPIRED.equals(status);
  }

  public boolean isProcessing(CoinOrderStatusEnum status) {
    return PARTIALLY_FILLED.equals(status) || PENDING_CANCEL.equals(status);
  }

  public boolean isInit(CoinOrderStatusEnum status) {
    return NEW.equals(status);
  }
}