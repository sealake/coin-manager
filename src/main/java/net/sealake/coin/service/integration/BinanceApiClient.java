package net.sealake.coin.service.integration;

import lombok.extern.slf4j.Slf4j;

import net.sealake.binance.api.client.BinanceApiRestClient;
import net.sealake.binance.api.client.domain.OrderSide;
import net.sealake.binance.api.client.domain.OrderType;
import net.sealake.binance.api.client.domain.TimeInForce;
import net.sealake.binance.api.client.domain.account.Account;
import net.sealake.binance.api.client.domain.account.AssetBalance;
import net.sealake.binance.api.client.domain.account.NewOrder;
import net.sealake.binance.api.client.domain.account.NewOrderResponse;
import net.sealake.binance.api.client.domain.account.Order;
import net.sealake.binance.api.client.domain.account.request.OrderStatusRequest;
import net.sealake.binance.api.client.domain.market.TickerPrice;
import net.sealake.binance.api.client.impl.BinanceApiRestClientImpl;
import net.sealake.coin.service.integration.entity.AccountInfo;
import net.sealake.coin.service.integration.entity.AssetInfo;
import net.sealake.coin.service.integration.entity.CoinOrderRequest;
import net.sealake.coin.service.integration.entity.CoinOrderResponse;
import net.sealake.coin.service.integration.entity.CoinPrice;
import net.sealake.coin.service.integration.entity.enums.CoinOrderSideEnum;
import net.sealake.coin.service.integration.entity.enums.CoinOrderStatusEnum;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Slf4j
public class BinanceApiClient implements BaseApiClient {

  private String apiKey;
  private String secretKey;

  private BinanceApiRestClient client = null;

  @Override
  public void reloadClient(String apiKey, String secretKey) {
    createClient(apiKey, secretKey);
  }

  @Override
  public Boolean testConnection() {
    if (null == client) {
      log.error("api client is null, apiKey: {}, secretKey: {}", apiKey, secretKey);
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
    // get account from coin remote
    Account account;

    try {
      account = client.getAccount();
    } catch (Exception ex) {
      log.error("binance getAccountInfo failed, apiKey: {}, error: {}", this.apiKey, ex);
      return null;
    }

    // convert to AccountInfo
    AccountInfo accountInfo = new AccountInfo();
    accountInfo.setCanDeposit(account.isCanDeposit());
    accountInfo.setCanTrade(account.isCanTrade());
    accountInfo.setCanWithdraw(account.isCanWithdraw());

    for (AssetBalance assetBalance : account.getBalances()) {
      BigDecimal freezeAmount = new BigDecimal(assetBalance.getLocked());
      BigDecimal availableAmount = new BigDecimal(assetBalance.getFree());

      // 过滤可用份额或者冻结份额大于0的
      if (availableAmount.compareTo(BigDecimal.ZERO) > 0 || freezeAmount.compareTo(BigDecimal.ZERO) > 0) {
        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setAssetName(assetBalance.getAsset());
        assetInfo.setFreezeAmount(freezeAmount);
        assetInfo.setAvailableAmount(availableAmount);

        accountInfo.getAssetInfos().add(assetInfo);
      }
    }

    return accountInfo;
  }

  @Override
  public CoinPrice getPrice(final String symbol) {
    TickerPrice price = client.getPrice(symbol);
    CoinPrice symbolPrice = new CoinPrice();
    symbolPrice.setSymbol(price.getSymbol());
    symbolPrice.setAskPrice(price.getPrice());
    symbolPrice.setBidPrice(price.getPrice());
    symbolPrice.setLastPrice(price.getPrice());
    return symbolPrice;
  }

  @Override
  public CoinOrderResponse sell(final CoinOrderRequest orderRequest) {
    CoinOrderResponse orderResponse = new CoinOrderResponse();

    // 构造交易请求
    NewOrder order = new NewOrder(orderRequest.getSymbol(),
        OrderSide.SELL,
        OrderType.LIMIT,
        TimeInForce.GTC,
        orderRequest.getQuantity(),
        orderRequest.getPrice()
    );

    // 测试请求合法性
    client.newOrderTest(order);

    // 发起请求
    NewOrderResponse newOrderResponse = client.newOrder(order);

    // 转换结果
    orderResponse.setSymbol(orderRequest.getSymbol());
    orderResponse.setPrice(newOrderResponse.getPrice());
    orderResponse.setQuantity(newOrderResponse.getOrigQty());
    orderResponse.setSide(CoinOrderSideEnum.SELL);
    orderResponse.setStatus(CoinOrderStatusEnum.valueOf(newOrderResponse.getStatus().name()));

    // binance平台的订单号是Long类型，在此转换成String类型
    orderResponse.getOrderIds().add(String.valueOf(newOrderResponse.getOrderId()));

    return orderResponse;
  }

  @Override
  public CoinOrderResponse buy(final CoinOrderRequest orderRequest) {
    // createClient();
    return null;
  }

  @Override
  public CoinOrderResponse getOrderStatus(final String symbol, final String orderId) {
    // binance orderId是Long类型，所以这里需要做类型转换
    OrderStatusRequest request = new OrderStatusRequest(symbol, Long.parseLong(orderId));
    Order order = client.getOrderStatus(request);

    CoinOrderResponse orderResponse = new CoinOrderResponse();
    // 转换结果
    orderResponse.setSymbol(order.getSymbol());
    orderResponse.setPrice(order.getPrice());
    orderResponse.setQuantity(order.getOrigQty());
    orderResponse.setSide(CoinOrderSideEnum.SELL);
    orderResponse.setStatus(CoinOrderStatusEnum.valueOf(order.getStatus().name()));
    orderResponse.getOrderIds().add(orderId);

    return orderResponse;
  }

  private void createClient(String aKey, String sKey) {

    // 如果 bourseAccount 配置有更新，需要重新生成 client。
    if (StringUtils.isNotBlank(aKey) && StringUtils.isNotBlank(sKey)) {

      // 如果ak或者sk发生变更，需要重新创建 client 对象
      if (!StringUtils.equals(aKey, this.apiKey)
          || !StringUtils.equals(sKey, this.secretKey)
          || client == null) {

        synchronized (this) {
          this.apiKey = aKey;
          this.secretKey = sKey;
          client = new BinanceApiRestClientImpl(aKey, sKey);
        }
      }
    }
  }
}