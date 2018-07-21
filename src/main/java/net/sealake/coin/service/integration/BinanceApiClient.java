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
import net.sealake.coin.util.Json;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Slf4j
public class BinanceApiClient implements BaseApiClient {

  private String apiKey;
  private String secretKey;

  private BinanceApiRestClient client = null;

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
      log.error("failed ping binance api server, apiKey: {}, secretKey: {}, exception: {}",
          apiKey, secretKey, ex);
      return false;
    }

    return true;
  }

  @Override
  public AccountInfo getAccountInfo() {
    // get account from binance remote
    Account account = client.getAccount();

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
    BeanUtils.copyProperties(price, symbol);
    CoinPrice symbolPrice = new CoinPrice();

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
    orderResponse.setPrice(newOrderResponse.getPrice());
    orderResponse.setOriginQuantity(newOrderResponse.getOrigQty());
    orderResponse.setOrderId(newOrderResponse.getOrderId());
    orderResponse.setClientOrderId(newOrderResponse.getClientOrderId());
    orderResponse.setSide(CoinOrderSideEnum.SELL);
    orderResponse.setStatus(CoinOrderStatusEnum.valueOf(newOrderResponse.getStatus().name()));


    return orderResponse;
  }

  @Override
  public CoinOrderResponse buy(final CoinOrderRequest orderRequest) {
    // createClient();
    return null;
  }

  @Override
  public CoinOrderResponse getOrderStatus(final String symbol, final Long orderId) {
    OrderStatusRequest request = new OrderStatusRequest(symbol, orderId);
    Order order = client.getOrderStatus(request);

    CoinOrderResponse orderResponse = new CoinOrderResponse();
    // 转换结果
    orderResponse.setPrice(order.getPrice());
    orderResponse.setOriginQuantity(order.getOrigQty());
    orderResponse.setOrderId(order.getOrderId());
    orderResponse.setClientOrderId(order.getClientOrderId());
    orderResponse.setSide(CoinOrderSideEnum.SELL);
    orderResponse.setStatus(CoinOrderStatusEnum.valueOf(order.getStatus().name()));

    return orderResponse;
  }

  @Override
  public CoinOrderResponse getOrderStatus(final String symbol, final String clientOrderId) {
    OrderStatusRequest request = new OrderStatusRequest(symbol, clientOrderId);
    Order order = client.getOrderStatus(request);

    CoinOrderResponse orderResponse = new CoinOrderResponse();
    // 转换结果
    orderResponse.setPrice(order.getPrice());
    orderResponse.setOriginQuantity(order.getOrigQty());
    orderResponse.setOrderId(order.getOrderId());
    orderResponse.setClientOrderId(order.getClientOrderId());
    orderResponse.setSide(CoinOrderSideEnum.SELL);
    orderResponse.setStatus(CoinOrderStatusEnum.valueOf(order.getStatus().name()));

    return orderResponse;
  }

  private void createClient(String apiKey, String secretKey) {

    // 如果 bourseAccount 配置有更新，需要重新生成 client。
    boolean configRefreshed = false;
    if (StringUtils.isNotBlank(apiKey) && StringUtils.isNotBlank(secretKey)) {

      // 如果ak或者sk发生变更，需要重新创建 client 对象
      if (!StringUtils.equals(apiKey, this.apiKey) || !StringUtils.equals(secretKey, this.secretKey)) {

        synchronized (this) {
          this.apiKey = apiKey;
          this.secretKey = secretKey;
          client = new BinanceApiRestClientImpl(apiKey, secretKey);
        }
      }
    }
  }
}