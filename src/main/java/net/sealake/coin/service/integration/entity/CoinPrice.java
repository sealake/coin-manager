package net.sealake.coin.service.integration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinPrice {

  /** 例如 XZCBTC BTCXZC */
  private String symbol;

  private String bidPrice;

  private String askPrice;

  private String lastPrice;
}