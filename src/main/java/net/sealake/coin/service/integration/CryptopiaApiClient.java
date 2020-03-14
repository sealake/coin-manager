package net.sealake.coin.service.integration;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.constants.ApiConstants;
import net.sealake.coin.service.integration.cryptopia.client.CryptopiaClient;
import net.sealake.coin.service.integration.cryptopia.client.CryptopiaClientImpl;
import net.sealake.coin.service.integration.cryptopia.models.CryptopiaBalance;
import net.sealake.coin.service.integration.cryptopia.models.CryptopiaMarket;
import net.sealake.coin.service.integration.cryptopia.models.CryptopiaTrade;
import net.sealake.coin.service.integration.cryptopia.models.CryptopiaTradeDetail;
import net.sealake.coin.service.integration.cryptopia.models.enums.CryptopiaTradeType;
import net.sealake.coin.service.integration.cryptopia.models.request.CryptopiaTradeRequest;
import net.sealake.coin.service.integration.entity.AccountInfo;
import net.sealake.coin.service.integration.entity.AssetInfo;
import net.sealake.coin.service.integration.entity.CoinOrderRequest;
import net.sealake.coin.service.integration.entity.CoinOrderResponse;
import net.sealake.coin.service.integration.entity.CoinPrice;
import net.sealake.coin.service.integration.entity.enums.CoinOrderSideEnum;
import net.sealake.coin.service.integration.entity.enums.CoinOrderStatusEnum;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class CryptopiaApiClient implements BaseApiClient {
  private static final String FETCH_HISTORY_COUNT = "100";
  private String apiKey;
  private String secretKey;

  private CryptopiaClient client = null;

  @Override
  public void reloadClient(String aKey, String sKey) {

    // 如果 bourseAccount 配置有更新，需要重新生成 client。
    if (StringUtils.isNotBlank(aKey) && StringUtils.isNotBlank(sKey)) {

      // 如果ak或者sk发生变更，需要重新创建 client 对象
      if (!StringUtils.equals(aKey, this.apiKey)
          || !StringUtils.equals(sKey, this.secretKey)
          || client == null) {

        synchronized (this) {
          this.apiKey = aKey;
          this.secretKey = sKey;
          client = new CryptopiaClientImpl(aKey, sKey);
        }
      }
    }
  }

  @Override
  public Boolean testConnection() {
    if (null == client) {
      log.error("cryptopia api client is null, apiKey: {}, secretKey: {}", apiKey, secretKey);
      return null;
    }

    try {
      client.ping();
    } catch (Exception ex) {
      log.error("failed ping coin api server, apiKey: {}, secretKey: {}, exception: {}",
          apiKey, secretKey, ex);
      return false;
    }

    return true;
  }

  @Override
  public AccountInfo getAccountInfo() {
    final AccountInfo accountInfo = new AccountInfo();
    accountInfo.setCanWithdraw(true);
    accountInfo.setCanWithdraw(true);
    accountInfo.setCanTrade(false);
    List<AssetInfo> assets = accountInfo.getAssetInfos();

    try {
      final List<CryptopiaBalance> balances = client.getAllBalances();
      for (CryptopiaBalance balance : balances) {
        // 过滤掉份额为0的货币资产
        if (balance.getTotal().compareTo(BigDecimal.ZERO) > 0) {
          AssetInfo assetInfo = new AssetInfo();
          assetInfo.setAssetName(StringUtils.strip(balance.getSymbol(), "\""));
          assetInfo.setAvailableAmount(balance.getAvailable());
          assetInfo.setFreezeAmount(balance.getTotal().subtract(balance.getAvailable()));

          assets.add(assetInfo);
        }
      }

      if (CollectionUtils.isNotEmpty(assets)) {
        accountInfo.setCanTrade(true);
      }

      return accountInfo;
    }catch (Exception ex) {
      log.error("cryptopia getAccountInfo failed, error: {}", ex);
    }

    return null;
  }

  /* symbol 例如， XZC卖出到BTC的symbol为: XZC_BTC */
  @Override
  public CoinPrice getPrice(String symbol) {
    final String marketSymbol = symbol.replace(ApiConstants.SEPERATOR_SLASH, ApiConstants.SEPERATOR_UNDERLINE);
    CryptopiaMarket market = client.getMarket(marketSymbol);

    final CoinPrice price = new CoinPrice();
    price.setSymbol(symbol);
    price.setBidPrice(market.getBidPrice().toPlainString());
    price.setAskPrice(market.getAskPrice().toPlainString());
    price.setLastPrice(market.getLastPrice().toPlainString());

    return price;
  }

  @Override
  public CoinOrderResponse sell(CoinOrderRequest orderRequest) {

    // 组装trade request
    final CryptopiaTradeRequest tradeRequest = new CryptopiaTradeRequest();
    tradeRequest.setAmount(new BigDecimal(orderRequest.getQuantity()));
    tradeRequest.setMarket(orderRequest.getSymbol());
    tradeRequest.setRate(new BigDecimal(orderRequest.getPrice()));
    tradeRequest.setType(CryptopiaTradeType.SELL);

    // 发起交易请求
    final CryptopiaTrade trade = client.submitTrade(tradeRequest);

    // 组装返回值
    final CoinOrderResponse response = new CoinOrderResponse();
    response.setSide(CoinOrderSideEnum.SELL);
    response.setQuantity(orderRequest.getQuantity());
    response.setPrice(orderRequest.getPrice());
    response.setStatus(CoinOrderStatusEnum.NEW);
    response.setSymbol(orderRequest.getSymbol());
    response.getOrderIds().add(String.valueOf(trade.getOrderId()));

    return response;
  }

  @Override
  public CoinOrderResponse buy(CoinOrderRequest orderRequest) {
    return null;
  }

  /**
   * 由于cryptopia不提供根据orderId获取交易详情的接口，我们根据交易类型在交易历史中查询，能够查找到说明交易已成功完成。
   */
  @Override
  public CoinOrderResponse getOrderStatus(String symbol, String orderId) {

    List<CryptopiaTradeDetail> tradeDetails = client.getTradeHistory(symbol,  FETCH_HISTORY_COUNT);

    // cryptopia的orderId是Long类型，在此做类型转换
    Long originOrderId = Long.parseLong(orderId);
    for (CryptopiaTradeDetail tradeDetail: tradeDetails) {
      if (tradeDetail.getTradeId().equals(originOrderId)) {
        final CoinOrderResponse response = new CoinOrderResponse();

        response.setSymbol(symbol);
        response.setPrice(tradeDetail.getRate().toPlainString());
        response.setStatus(CoinOrderStatusEnum.FILLED);
        response.setQuantity(tradeDetail.getAmount().toPlainString());
        response.setSide(CoinOrderSideEnum.getByCode(tradeDetail.getType()));

        return response;
      }
    }
    return null;
  }
}