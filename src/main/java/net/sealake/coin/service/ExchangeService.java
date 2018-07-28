package net.sealake.coin.service;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.constants.ApiConstants;
import net.sealake.coin.entity.BourseAccount;
import net.sealake.coin.entity.CoinAccount;
import net.sealake.coin.entity.CoinOrder;
import net.sealake.coin.entity.CoinTask;
import net.sealake.coin.entity.enums.BoursePlatform;
import net.sealake.coin.entity.enums.CoinTaskStatus;
import net.sealake.coin.repository.BourseAccountRepository;
import net.sealake.coin.repository.CoinAccountRepository;
import net.sealake.coin.repository.CoinOrderRepository;
import net.sealake.coin.repository.CoinTaskRepository;
import net.sealake.coin.service.integration.ApiClientManager;
import net.sealake.coin.service.integration.BaseApiClient;
import net.sealake.coin.service.integration.entity.CoinOrderRequest;
import net.sealake.coin.service.integration.entity.CoinOrderResponse;
import net.sealake.coin.service.integration.entity.CoinPrice;
import net.sealake.coin.service.integration.entity.enums.CoinOrderStatusEnum;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 交易逻辑
 */
@Slf4j
@Service
public class ExchangeService {

  @Autowired
  private ApiClientManager apiClientManager;

  @Autowired
  private BourseAccountRepository bourseAccountRepository;

  @Autowired
  private CoinAccountRepository coinAccountRepository;

  @Autowired
  private CoinTaskRepository coinTaskRepository;

  @Autowired
  private CoinOrderRepository coinOrderRepository;

  /**
   * 卖出操作
   */
  @Transactional
  public void sell(CoinTask task) {
    // 获取交易所client对象
    BaseApiClient apiClient = null;
    try {
      apiClient = this.getApiClient(task);
      if (apiClient == null) {
        log.warn("交易所api client配置有问题，任务不执行, task: {}", task);
        return;
      }
    } catch (Exception ex) {
      log.error("获取交易所对象失败，error: {}", ex);
      return;
    }

    // 锁定coin账号
    CoinAccount coinAccount = coinAccountRepository.findByIdForUpdate(task.getCoinId());
    if (coinAccount == null) {
      log.warn("获取不到coin账户，coinId: {}", coinAccount.getId());
      return;
    }
    // 获取当前coin的行情信息
    CoinPrice coinPrice = apiClient.getPrice(task.getSymbol());

    try {
      // 构建远程订单请求
      CoinOrderRequest orderRequest = new CoinOrderRequest();
      orderRequest.setQuantity(task.getQuantity().toPlainString());
      orderRequest.setSymbol(task.getSymbol());
      orderRequest.setPrice(coinPrice.getLastPrice());

      // 发起卖出请求
      CoinOrderResponse orderResponse = apiClient.sell(orderRequest);

      // 根据卖出结果补全coinTask字段并落库
      task.setTaskStatus(CoinTaskStatus.COMMIT);
      // bsnId 设置为orderId连接起来的字符串
      String bsnIdStr = StringUtils.join(orderResponse.getOrderIds(), ApiConstants.SEPERATOR_DOUBLE_UNDERLINE);
      task.setBsnId(bsnIdStr);
      coinTaskRepository.save(task);

      // 填充本地订单数据并落库，交易所可能会生成多笔交易，这种情况本地需要落多笔单据
      for (String orderId : orderResponse.getOrderIds()) {
        CoinOrder coinOrder = buildOrderFromTask(task, orderId, new BigDecimal(orderResponse.getPrice()));
        coinOrderRepository.save(coinOrder);
      }

      // 解锁预冻结金额
      BigDecimal preFreezeAmount = coinAccount.getPreFreezeAmount().subtract(task.getQuantity());
      coinAccount.setPreFreezeAmount(preFreezeAmount);
      coinAccountRepository.save(coinAccount);

    } catch (Exception ex) {
      log.error("coin交易失败，task {}, error {}", task, ex);

      // 捕获到异常，删除coin_task表对应数据
      coinTaskRepository.delete(task.getId());

      // 解锁预冻结金额
      BigDecimal preFreezeAmount = coinAccount.getPreFreezeAmount().subtract(task.getQuantity());
      coinAccount.setPreFreezeAmount(preFreezeAmount);
      coinAccountRepository.save(coinAccount);
    }
  }

  /*
   * 跟踪任务状态
   */
  @Transactional
  public void updateSellOrderStatus(CoinTask task) {

    // cryptopia渠道未提供订单查询接口，直接清除该平台commit状态的task数据。
    if (task.getPlatform().equals(BoursePlatform.CRYPTOPIA)) {
      coinTaskRepository.delete(task.getId());
      return;
    }

    BaseApiClient apiClient = getApiClient(task);
    if (apiClient == null) {
      log.warn("交易所api client配置有问题，任务不执行, task: {}", task);
      return;
    }

    List<String> orderIds = Arrays.asList(StringUtils.split(task.getBsnId(), ApiConstants.SEPERATOR_DOUBLE_UNDERLINE));
    List<CoinOrder> orders = coinOrderRepository.findByTaskId(task.getId());

    // 补充task表中存在但未落到订单表中的订单
    List<String> lostOrderIds = filterLostOrders(orderIds, orders);
    if (CollectionUtils.isEmpty(lostOrderIds)) {
      for (String orderId : lostOrderIds) {
        log.warn("对应commit阶段的task没有订单数据，补插本地订单。task: {}, lost order id: {}", task, orderId);
        CoinOrder order = buildOrderFromTask(task, orderId, null);
        order = coinOrderRepository.save(order);

        if (orders == null) {
          orders = new ArrayList<>();
        }
        orders.add(order);
      }
    }

    try {
      // 订单是否全部完成
      boolean allFinished = true;

      // 遍历当前taskId对应的所有订单，从交易所更新订单状态
      for (CoinOrder order : orders) {
        // 如果当前订单已经到达终态，则跳过不再检查
        if (CoinTaskStatus.isFinished(order.getTaskStatus())) {
          continue;
        }

        // 从交易所获取订单状态
        CoinOrderResponse response = apiClient.getOrderStatus(task.getSymbol(), order.getBsnId());
        if (response == null) {
          // 从交易所获取到的数据为空
          allFinished = false;
          continue;
        }

        // 如果到达终态（成功、失败）,更新order表状态，删除task数据
        if (CoinOrderStatusEnum.isSuccess(response.getStatus())) {
          order.setPrice(new BigDecimal(response.getPrice()));
          order.setQuantity(new BigDecimal(response.getQuantity()));
          order.setTaskStatus(CoinTaskStatus.SUCCESS);
          coinOrderRepository.save(order);
        } else if (CoinOrderStatusEnum.isFail(response.getStatus())) {
          order.setPrice(new BigDecimal(response.getPrice()));
          order.setQuantity(new BigDecimal(response.getQuantity()));
          order.setTaskStatus(CoinTaskStatus.FAIL);
          coinOrderRepository.save(order);
        } else {
          // 如果未达终态，则
          allFinished = false;
        }
      }

      // 如果该任务对应的所有订单都到达了终态，则删除task表数据
      if (allFinished) {
        coinTaskRepository.delete(task.getId());
      }
    } catch (Exception ex) {
      log.info("获取账单状态失败, task: {}, error: {}", task, ex);
    }
  }

  private BaseApiClient getApiClient(CoinTask task) {
    // 对于交易所和coin账户都不存在的情况，订单不删除
    BourseAccount bourseAccount = bourseAccountRepository.findOne(task.getBourseId());
    if (bourseAccount == null) {
      log.warn("交易所账号不存在, task不执行。taskId: {}, bourseAccount Id: {}", task.getId(), task.getBourseId());
      return null;
    }

    return apiClientManager.getApiClient(bourseAccount);
  }

  private CoinOrder buildOrderFromTask(CoinTask task, String orderId, BigDecimal price) {
    CoinOrder coinOrder = new CoinOrder();

    coinOrder.setBsnId(orderId);  // 交易所平台的账务流水号
    coinOrder.setBourseId(task.getBourseId());
    coinOrder.setCoinId(task.getCoinId());
    coinOrder.setTaskId(task.getId());

    coinOrder.setPlatform(task.getPlatform());
    coinOrder.setSymbol(task.getSymbol());
    coinOrder.setQuantity(task.getQuantity());
    if (price != null) {
      coinOrder.setPrice(price);
    }

    coinOrder.setTaskType(task.getTaskType());
    coinOrder.setTaskStatus(CoinTaskStatus.COMMIT);
    coinOrder.setExchangeTime(DateTime.now());

    return coinOrder;
  }

  // 过滤task表中存在而本地订单表中不存在的订单号
  private List<String> filterLostOrders(List<String> orderIds, List<CoinOrder> orders) {
    List<String> filterdOrderIds = new ArrayList<>();
    if (CollectionUtils.isEmpty(orderIds)) {
      return filterdOrderIds;
    }

    if (CollectionUtils.isEmpty(orders)) {
      filterdOrderIds.addAll(orderIds);
      return filterdOrderIds;
    }

    // 如果task中的orderId在订单表中不存在，则放入filterdOrderIds列表中
    boolean exists = false;
    for (String orderId : orderIds) {
      for (CoinOrder coinOrder : orders) {
        if (StringUtils.equalsIgnoreCase(orderId.trim(), coinOrder.getBsnId().trim())) {
          exists = true;
          break;
        }
      }

      if (exists) {
        exists = false;
      } else {
        filterdOrderIds.add(orderId);
      }
    }

    return filterdOrderIds;
  }
}
