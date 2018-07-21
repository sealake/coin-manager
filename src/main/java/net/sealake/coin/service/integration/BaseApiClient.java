package net.sealake.coin.service.integration;

import net.sealake.coin.service.integration.entity.AccountInfo;
import net.sealake.coin.service.integration.entity.CoinOrderRequest;
import net.sealake.coin.service.integration.entity.CoinOrderResponse;
import net.sealake.coin.service.integration.entity.CoinPrice;

public interface BaseApiClient {

  /**
   * 测试连通性
   */
  Boolean testConnection();

  /**
   * 获取账户详情
   */
  AccountInfo getAccountInfo();

  /**
   * 获取当前行情
   */
  CoinPrice getPrice(final String symbol);

  /**
   * 卖出
   */
  CoinOrderResponse sell(final CoinOrderRequest orderRequest);

  /**
   * 买入
   */
  CoinOrderResponse buy(final CoinOrderRequest orderRequest);

  /**
   * 查看交易状态
   */
  CoinOrderResponse getOrderStatus(final String symbol, final Long orderId);

  CoinOrderResponse getOrderStatus(final String symbol, final String orderId);
}