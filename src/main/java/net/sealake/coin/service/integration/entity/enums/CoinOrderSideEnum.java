package net.sealake.coin.service.integration.entity.enums;

import org.apache.commons.lang3.StringUtils;

public enum CoinOrderSideEnum {
  BUY,
  SELL
  ;

  public static CoinOrderSideEnum getByCode(String code) {
    if (StringUtils.equalsIgnoreCase("buy", code)) {
      return BUY;
    }
    if (StringUtils.equalsIgnoreCase("sell", code)) {
      return SELL;
    }

    return null;
  }
}