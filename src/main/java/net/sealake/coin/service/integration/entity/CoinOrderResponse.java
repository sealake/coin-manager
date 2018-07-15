package net.sealake.coin.service.integration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.sealake.coin.service.integration.entity.enums.CoinOrderSideEnum;
import net.sealake.coin.service.integration.entity.enums.CoinOrderStatusEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinOrderResponse {

  private Boolean submit;

  private String symbol;

  private Long orderId;

  private String clientOrderId;

  private String price;

  /** origin quantity*/
  private String originQuantity;

  private CoinOrderStatusEnum status;

  private CoinOrderSideEnum side;

}