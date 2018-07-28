package net.sealake.coin.service.integration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.sealake.coin.service.integration.entity.enums.CoinOrderSideEnum;
import net.sealake.coin.service.integration.entity.enums.CoinOrderStatusEnum;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinOrderResponse {

  private String symbol;

  /**
   * 客户端发起一笔交易之后，后台可能会生成多笔订单，对应多个订单号
   */
  private List<String> orderIds = new ArrayList<>();

  private String price;

  /** origin quantity*/
  private String quantity;

  private CoinOrderStatusEnum status;

  private CoinOrderSideEnum side;

}