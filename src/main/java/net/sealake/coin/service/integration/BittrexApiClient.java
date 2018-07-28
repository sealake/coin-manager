package net.sealake.coin.service.integration;

import com.github.ccob.bittrex4j.BittrexExchange;
import com.github.ccob.bittrex4j.dao.Balance;
import com.github.ccob.bittrex4j.dao.MarketSummary;
import com.github.ccob.bittrex4j.dao.Order;
import com.github.ccob.bittrex4j.dao.Response;
import com.github.ccob.bittrex4j.dao.UuidResult;
import com.sun.javafx.binding.StringFormatter;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.service.integration.entity.AccountInfo;
import net.sealake.coin.service.integration.entity.AssetInfo;
import net.sealake.coin.service.integration.entity.CoinOrderRequest;
import net.sealake.coin.service.integration.entity.CoinOrderResponse;
import net.sealake.coin.service.integration.entity.CoinPrice;
import net.sealake.coin.service.integration.entity.enums.CoinOrderSideEnum;
import net.sealake.coin.service.integration.entity.enums.CoinOrderStatusEnum;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class BittrexApiClient implements BaseApiClient {
  private String apiKey;
  private String secretKey;

  private BittrexExchange client = null;

  @Override
  public void reloadClient(String akey, String skey) {

    // 如果 bourseAccount 配置有更新，需要重新生成 client。
    if (StringUtils.isNotBlank(akey) && StringUtils.isNotBlank(skey)) {

      // 如果ak或者sk发生变更，需要重新创建 client 对象
      if (!StringUtils.equals(akey, this.apiKey)
          || !StringUtils.equals(skey, this.secretKey)
          || client == null) {

        synchronized (this) {
          this.apiKey = akey;
          this.secretKey = skey;
          try {
            client = new BittrexExchange(this.apiKey, this.secretKey);
          } catch (IOException ex) {
            log.error("failed create bittrex client, error: {}", ex);
          }
        }
      }
    }
  }

  @Override
  public Boolean testConnection() {
    if (null == client) {
      log.error("bittrex api client is null, apiKey: {}, secretKey: {}", apiKey, secretKey);
      return null;
    }

    try {
      Response<Balance> balanceResponse = client.getBalance("BTC");
      return balanceResponse.isSuccess();
    } catch (Exception ex) {
      log.error("testConnection failed, error {}", ex);
    }

    return false;
  }

  @Override
  public AccountInfo getAccountInfo() {

    final AccountInfo accountInfo = new AccountInfo();
    accountInfo.setCanWithdraw(true);
    accountInfo.setCanWithdraw(true);
    accountInfo.setCanTrade(false);
    final List<AssetInfo> assets = accountInfo.getAssetInfos();

    try {
      final Response<Balance[]> balancesResponse = client.getBalances();
      if (balancesResponse.isSuccess()) {
        Balance[] balances = balancesResponse.getResult();
        for (Balance balance: balances) {
          AssetInfo assetInfo = new AssetInfo();
          assetInfo.setAssetName(balance.getCurrency());
          assetInfo.setAvailableAmount(new BigDecimal(balance.getAvailable()));
          assetInfo.setFreezeAmount(new BigDecimal(balance.getBalance()).subtract(new BigDecimal(balance.getAvailable())));

          assets.add(assetInfo);
        }

        if (CollectionUtils.isNotEmpty(assets)) {
          accountInfo.setCanTrade(true);
        }
        return accountInfo;
      }
    } catch (Exception ex) {
      log.error("bittrex getAccountInfo failed, error: {}", ex);
    }
    return null;
  }

  @Override
  public CoinPrice getPrice(String symbol) {

    Response<MarketSummary[]> summaryResponse = client.getMarketSummaryV1(symbol);
    if (!summaryResponse.isSuccess()) {
      // 如果response不成功
      final String errorStr = String.format("failed get price of %s, reason %s", symbol, summaryResponse.getMessage());
      throw new RuntimeException(errorStr);
    }

    MarketSummary[] markets = summaryResponse.getResult();
    if (markets == null || markets.length == 0) {
      // 如果获取到的行情数组为空
      final String errorStr = String.format("failed get price of %s, empty marketsummary", symbol);
      throw new RuntimeException(errorStr);
    }

    // 构造行情结果
    final MarketSummary marketSummary = markets[0];
    final CoinPrice price = new CoinPrice();
    price.setSymbol(symbol);
    price.setBidPrice(marketSummary.getBid().toPlainString());
    price.setAskPrice(marketSummary.getAsk().toPlainString());
    price.setLastPrice(marketSummary.getLast().toPlainString());

    return price;
  }

  @Override
  public CoinOrderResponse sell(CoinOrderRequest orderRequest) {
    final BigDecimal quantity = new BigDecimal(orderRequest.getQuantity());
    final BigDecimal price = new BigDecimal(orderRequest.getPrice());
    final Response<UuidResult> sellResult = client.sellLimit(orderRequest.getSymbol(),
        quantity.doubleValue(), price.doubleValue());

    if (!sellResult.isSuccess()) {
      final String errorStr = String.format("failed sell coin, symbol %s, quantity %s, price %s, reason %s",
          orderRequest.getSymbol(), orderRequest.getQuantity(), orderRequest.getPrice(), sellResult.getMessage());
      throw new RuntimeException(errorStr);
    }

    // 构造返回值数据
    final UuidResult result = sellResult.getResult();
    final CoinOrderResponse orderResponse = new CoinOrderResponse();
    orderResponse.setSymbol(orderRequest.getSymbol());
    orderResponse.setPrice(orderRequest.getPrice());
    orderResponse.setQuantity(orderRequest.getQuantity());
    orderResponse.setSide(CoinOrderSideEnum.SELL);
    orderResponse.setStatus(CoinOrderStatusEnum.NEW);
    orderResponse.getOrderIds().add(result.getUuid());

    return orderResponse;
  }

  @Override
  public CoinOrderResponse buy(CoinOrderRequest orderRequest) {
    return null;
  }

  @Override
  public CoinOrderResponse getOrderStatus(String symbol, String orderId) {
    Response<Order> apiResponse = client.getOrder(orderId);
    if (!apiResponse.isSuccess()) {
      final String errorStr = String.format("failed get order status, orderId: %s, reason: %s",
          orderId, apiResponse.getMessage());
      throw new RuntimeException(errorStr);
    }

    final Order apiResult = apiResponse.getResult();
    if (apiResponse.getResult() == null) {
      return null;
    }
    final CoinOrderResponse orderResponse = new CoinOrderResponse();
    orderResponse.setSymbol(apiResult.getExchange());
    orderResponse.setPrice(String.valueOf(apiResult.getLimit()));
    orderResponse.setQuantity(String.valueOf(apiResult.getQuantity()));
    if (!apiResult.isOpen()) {
      orderResponse.setStatus(CoinOrderStatusEnum.FILLED);
    }

    return orderResponse;
  }
}
