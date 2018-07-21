package net.sealake.coin.service;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.entity.BourseAccount;
import net.sealake.coin.entity.CoinAccount;
import net.sealake.coin.entity.CoinOrder;
import net.sealake.coin.entity.CoinTask;
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

import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
   * @param task
   */
  @Transactional
  public void sell(CoinTask task) {
    // 获取交易所client对象
    BaseApiClient apiClient = this.getApiClient(task);
    if (apiClient == null) {
      log.warn("交易所api client配置有问题，任务不执行, task: {}", task);
      return;
    }

    // 锁定coin账号
    CoinAccount coinAccount = coinAccountRepository.findByIdForUpdate(task.getCoinId());
    if (coinAccount == null) {
      log.warn("获取不到coin账户，coinId: {}", coinAccount.getId());
      return;
    }

    // 构建 订单数据
    CoinOrder coinOrder = new CoinOrder();
    BeanUtils.copyProperties(task, coinOrder);
    coinOrder.setId(null);   // id应自动生成
    coinOrder.setTaskId(task.getId());

    try {

      // 获取当前coin的
      CoinPrice coinPrice = apiClient.getPrice(task.getSymbol());

      // 构建远程订单请求
      CoinOrderRequest orderRequest = new CoinOrderRequest();
      orderRequest.setQuantity(task.getQuantity().toPlainString());
      orderRequest.setSymbol(task.getSymbol());
      orderRequest.setPrice(coinPrice.getPrice());

      // 填充本地订单数据
      coinOrder.setPrice(new BigDecimal(coinPrice.getPrice()));
      coinOrder.setExchangeTime(DateTime.now());

      // 发起卖出请求
      CoinOrderResponse orderResponse = apiClient.sell(orderRequest);

      // 根据卖出结果补全coinTask和coinOrder字段并落库
      task.setTaskStatus(CoinTaskStatus.COMMIT);
      task.setBsnId(orderResponse.getClientOrderId());
      coinTaskRepository.save(task);

      coinOrder.setTaskStatus(CoinTaskStatus.COMMIT);
      coinOrder.setBsnId(orderResponse.getClientOrderId());  // 交易所平台的账务流水号
      coinOrderRepository.save(coinOrder);

      // 解锁预冻结金额
      BigDecimal preFreezeAmount = coinAccount.getPreFreezeAmount().subtract(task.getQuantity());
      coinAccount.setPreFreezeAmount(preFreezeAmount);
      coinAccountRepository.save(coinAccount);

    } catch (Exception ex) {
      log.error("coin交易失败，task {}, error {}", task, ex);

      // 设置task和coinOrder为失败状态
      coinTaskRepository.delete(task.getId());
    }
  }

  /*
   * 跟踪任务状态
   */
  @Transactional
  public void updateSellOrderStatus(CoinTask task) {
    BaseApiClient apiClient = getApiClient(task);
    if (apiClient == null) {
      log.warn("交易所api client配置有问题，任务不执行, task: {}", task);
      return;
    }

    CoinOrder order = coinOrderRepository.findByTaskId(task.getId());
    if (order == null) {
      log.warn("对应commit阶段的task没有订单数据，补插一条订单。task: {}", task);
      order = new CoinOrder();
      BeanUtils.copyProperties(task, order);
      order.setId(null);
      order.setTaskId(task.getId());
      order.setExchangeTime(DateTime.now());
      order = coinOrderRepository.save(order);
    }

    try {
      CoinOrderResponse response = apiClient.getOrderStatus(task.getSymbol(), task.getBsnId());

      // 如果到达终态（成功、失败）,更新order表状态，删除task数据
      if (CoinOrderStatusEnum.isSuccess(response.getStatus())) {
        order.setPrice(new BigDecimal(response.getPrice()));
        order.setTaskStatus(CoinTaskStatus.SUCCESS);
        coinOrderRepository.save(order);
        coinTaskRepository.delete(task.getId());
      } else if (CoinOrderStatusEnum.isFail(response.getStatus())) {
        order.setPrice(new BigDecimal(response.getPrice()));
        order.setTaskStatus(CoinTaskStatus.FAIL);
        coinOrderRepository.save(order);
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
}
