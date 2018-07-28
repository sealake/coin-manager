package net.sealake.coin.service.integration.cryptopia.models.enums;

import lombok.Getter;

@Getter
public enum CryptopiaTradeType {

  BUY("Buy"),
  SELL("Sell");

  private final String label;

  private CryptopiaTradeType(String label) {
    this.label = label;
  }

  public static CryptopiaTradeType byLabel(String label) {
    for (CryptopiaTradeType st : values()) {
      if (st.label.equals(label)) {
        return st;
      }
    }
    return null;
  }
}