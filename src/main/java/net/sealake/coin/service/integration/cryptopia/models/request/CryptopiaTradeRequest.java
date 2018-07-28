package net.sealake.coin.service.integration.cryptopia.models.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.sealake.coin.service.integration.cryptopia.models.enums.CryptopiaTradeType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CryptopiaTradeRequest {
  /**
   * The market symbol of the trade e.g. 'DOT/BTC' (not required if 'TradePairId' supplied).
   */
  private String market;

  /**
   * The Cryptopia tradepair identifier of trade e.g. '100' (not required if 'Market' supplied).
   */
  private String tradePairId;

  /**
   *  The type of trade e.g. 'Buy' or 'Sell'.
   */
  private CryptopiaTradeType type;

  /**
   * The rate or price to pay for the coins, e.g. 0.00000034.
   */
  private BigDecimal rate;

  /**
   * The amount of coins to buy, e.g. 123.00000000.
   */
  private BigDecimal amount;
}