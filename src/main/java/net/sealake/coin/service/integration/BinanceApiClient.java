package net.sealake.coin.service.integration;

import lombok.extern.slf4j.Slf4j;

import net.sealake.binance.api.client.BinanceApiRestClient;
import net.sealake.binance.api.client.domain.OrderSide;
import net.sealake.binance.api.client.domain.OrderType;
import net.sealake.binance.api.client.domain.TimeInForce;
import net.sealake.binance.api.client.domain.account.NewOrder;
import net.sealake.binance.api.client.domain.account.NewOrderResponse;
import net.sealake.binance.api.client.domain.account.Order;
import net.sealake.binance.api.client.domain.account.request.OrderStatusRequest;
import net.sealake.binance.api.client.domain.market.TickerPrice;
import net.sealake.binance.api.client.impl.BinanceApiRestClientImpl;
import net.sealake.coin.entity.BourseAccount;
import net.sealake.coin.entity.enums.BourseEnum;
import net.sealake.coin.service.integration.entity.CoinOrderRequest;
import net.sealake.coin.service.integration.entity.CoinOrderResponse;
import net.sealake.coin.service.integration.entity.CoinPrice;
import net.sealake.coin.service.integration.entity.enums.CoinOrderSideEnum;
import net.sealake.coin.service.integration.entity.enums.CoinOrderStatusEnum;
import net.sealake.coin.util.Json;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("binanceApiClient")
public class BinanceApiClient implements BaseApiClient {

  @Autowired
  private BourseCoinConfigLoader configLoader;

  private String apiKey;
  private String secretKey;
  private BinanceApiRestClient client = null;

  @Override
  public Boolean testConnection() {
    createClient();

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
  public CoinPrice getPrice(final String symbol) {
    createClient();

    TickerPrice price = client.getPrice(symbol);
    BeanUtils.copyProperties(price, symbol);
    CoinPrice symbolPrice = new CoinPrice();

    return symbolPrice;
  }

  @Override
  public CoinOrderResponse sell(final CoinOrderRequest orderRequest) {

    createClient();
    CoinOrderResponse orderResponse = new CoinOrderResponse();

    try {
      // 获取价格
      CoinPrice price = this.getPrice(orderRequest.getSymbol());

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
      orderResponse.setSubmit(true);
      orderResponse.setPrice(newOrderResponse.getPrice());
      orderResponse.setOriginQuantity(newOrderResponse.getOrigQty());
      orderResponse.setPrice(newOrderResponse.getPrice());
      orderResponse.setOrderId(newOrderResponse.getOrderId());
      orderResponse.setClientOrderId(newOrderResponse.getClientOrderId());
      orderResponse.setSide(CoinOrderSideEnum.SELL);
      orderResponse.setStatus(CoinOrderStatusEnum.valueOf(newOrderResponse.getStatus().name()));

    } catch (Exception ex) {
      log.error("failed sell coin, request {}, error: {}", Json.dumps(orderRequest), ex);
      orderResponse.setSubmit(false);
    }

    return orderResponse;
  }

  @Override
  public CoinOrderResponse buy(final CoinOrderRequest orderRequest) {
    // createClient();
    return null;
  }

  @Override
  public CoinOrderResponse getOrderStatus(final String symbol, final Long orderId) {
    createClient();
    OrderStatusRequest request = new OrderStatusRequest(symbol, orderId);
    Order order = client.getOrderStatus(request);

    CoinOrderResponse orderResponse = new CoinOrderResponse();
    // 转换结果
    orderResponse.setSubmit(true);
    orderResponse.setPrice(order.getPrice());
    orderResponse.setOriginQuantity(order.getOrigQty());
    orderResponse.setPrice(order.getPrice());
    orderResponse.setOrderId(order.getOrderId());
    orderResponse.setClientOrderId(order.getClientOrderId());
    orderResponse.setSide(CoinOrderSideEnum.SELL);
    orderResponse.setStatus(CoinOrderStatusEnum.valueOf(order.getStatus().name()));

    return orderResponse;
  }

  @Override
  public CoinOrderResponse getOrderStatus(final String symbol, final String clientOrderId) {
    createClient();
    OrderStatusRequest request = new OrderStatusRequest(symbol, clientOrderId);
    Order order = client.getOrderStatus(request);

    CoinOrderResponse orderResponse = new CoinOrderResponse();
    // 转换结果
    orderResponse.setSubmit(true);
    orderResponse.setPrice(order.getPrice());
    orderResponse.setOriginQuantity(order.getOrigQty());
    orderResponse.setPrice(order.getPrice());
    orderResponse.setOrderId(order.getOrderId());
    orderResponse.setClientOrderId(order.getClientOrderId());
    orderResponse.setSide(CoinOrderSideEnum.SELL);
    orderResponse.setStatus(CoinOrderStatusEnum.valueOf(order.getStatus().name()));

    return orderResponse;
  }

  private void createClient() {
    BourseAccount bourseAccount = configLoader.getBourseAccountMap().get(BourseEnum.BINANCE.name());
    if (bourseAccount == null) {
      return;
    }

    // 如果 bourseAccount 配置有更新，需要重新生成 client。
    boolean configRefreshed = false;
    if (StringUtils.isNotBlank(bourseAccount.getApiKey()) && StringUtils.isNotBlank(bourseAccount.getSecretKey())) {

      // FIXME: 每次请求都检测配置是否发生更新，此处可进行优化
      if (!StringUtils.equals(bourseAccount.getApiKey(), this.apiKey)
          || !StringUtils.equals(bourseAccount.getSecretKey(), this.secretKey)) {

        synchronized (this) {
          this.apiKey = bourseAccount.getApiKey();
          this.secretKey = bourseAccount.getSecretKey();
          client = new BinanceApiRestClientImpl(apiKey, secretKey);
        }
      }
    }
  }
}