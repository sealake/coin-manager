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

  /** 汇率行情 */
  private String price;
}