package net.sealake.coin.service.integration.cryptopia.client;

import net.sealake.coin.service.integration.cryptopia.models.CryptopiaBalance;
import net.sealake.coin.service.integration.cryptopia.models.CryptopiaMarket;
import net.sealake.coin.service.integration.cryptopia.models.CryptopiaTrade;
import net.sealake.coin.service.integration.cryptopia.models.CryptopiaTradeDetail;
import net.sealake.coin.service.integration.cryptopia.models.request.CryptopiaTradeRequest;

import java.util.List;

public interface CryptopiaClient {
  /**
   * 测试连通性
   */
  void ping();

  /**
   * 获取market信息，包括价格、行情等
   *
   * @param symbol TradePairId or MarketName， 比如 100， XZC_BTC
   * @return market实例
   */
  CryptopiaMarket getMarket(final String symbol);

  /**
   * 查询所有货币资产
   */
  List<CryptopiaBalance> getAllBalances();

  /**
   * 查询指定货币资产信息
   *
   * @param currency 币种
   */
  CryptopiaBalance getBalance(String currency);

  /**
   * 发起交易
   * @param request 交易参数
   * @return Trade实例，包含交易的id等信息
   */
  CryptopiaTrade submitTrade(CryptopiaTradeRequest request);

  /**
   * 获取交易历史
   * @param symbol The market symbol of the history to return, e.g. 'DOT/BTC'
   * @param count The maximum amount of history to return e.g. '10'
   * @return 历史交易详情的列表
   */
  List<CryptopiaTradeDetail> getTradeHistory(String symbol, String count);
}