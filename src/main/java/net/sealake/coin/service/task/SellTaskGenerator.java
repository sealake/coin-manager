
package net.sealake.coin.service.task;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.constants.ApiConstants;
import net.sealake.coin.entity.BourseAccount;
import net.sealake.coin.entity.CoinAccount;
import net.sealake.coin.entity.CoinSellStrategy;
import net.sealake.coin.entity.CoinTask;
import net.sealake.coin.entity.enums.CoinTaskStatus;
import net.sealake.coin.entity.enums.CoinTaskType;
import net.sealake.coin.repository.CoinAccountRepository;
import net.sealake.coin.repository.CoinTaskRepository;
import net.sealake.coin.service.integration.BourseCoinConfigLoader;
import net.sealake.coin.service.integration.entity.AssetInfo;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 卖出task生成器
 */
@Slf4j
@Service
public class SellTaskGenerator {

  @Autowired
  private CoinAccountRepository coinAccountRepository;

  @Autowired
  private CoinTaskRepository taskRepository;

  @Autowired
  private BourseCoinConfigLoader bourseCoinConfigLoader;

  private String getSymbol(BourseAccount bourseAccount, CoinAccount coinAccount) {
    switch (bourseAccount.getPlatform()) {
      case BINANCE:
        // 以XZC卖出到BTC为例，下同。
        // XZCBTC
        return coinAccount.getName() + coinAccount.getSellDecision();

      case BITREX:
        // BTC-XZC
        return coinAccount.getSellDecision() + ApiConstants.SEPERATOR_MINUS_SIGN + coinAccount.getName();

      case CRYPTOPIA:
        // XZC/BTC
        return coinAccount.getName() + ApiConstants.SEPERATOR_SLASH + coinAccount.getName();

      default:
        return null;
    }
  }

  @Transactional
  public void generatorTask(BourseAccount bourseAccount, long coinAccountId, AssetInfo assetInfo) {

    // 锁定当前账户
    CoinAccount coinAccount = coinAccountRepository.findByIdForUpdate(coinAccountId);

    // 可用数量为目前可用数量 - 预冻结数量（分批次委托的数量, 在coin-manager侧逻辑锁定）
    BigDecimal availableQuantity = assetInfo.getAvailableAmount().subtract(coinAccount.getPreFreezeAmount());
    if (availableQuantity.compareTo(BigDecimal.ZERO) <= 0) {
      log.info("当前coin账户可用数量 <= 0，coinAccout: {}, assetInfo: {}, availableQuantity: {}",
          coinAccount, assetInfo, availableQuantity);
      return;
    }

    int splitSize;
    CoinSellStrategy strategy = coinAccount.getCoinSellStrategy();
    if (strategy == null) {
      splitSize = 1;
    } else {
      splitSize = (int) Math.ceil(
          availableQuantity.divide(strategy.getQuotaPerSell(), 8, BigDecimal.ROUND_UP).doubleValue());
    }

    // 如果未设置strategy 或者 不拆分任务
    if (1 == splitSize) {
      CoinTask task = new CoinTask();
      task.setBourseId(bourseAccount.getId());
      task.setCoinId(coinAccount.getId());
      task.setQuantity(availableQuantity);
      task.setExecuteTime(DateTime.now());
      task.setSymbol(this.getSymbol(bourseAccount, coinAccount));
      task.setTaskType(CoinTaskType.SELL);
      task.setTaskStatus(CoinTaskStatus.INIT);

      // 保存任务到数据库
      task = taskRepository.save(task);
      log.info("生成sell任务, {}", task);

      return;
    }

    // 按照strategy拆分交易
    for (int i = 0; i < splitSize; ++i) {
      CoinTask task = new CoinTask();
      task.setBourseId(bourseAccount.getId());
      task.setCoinId(coinAccount.getId());
      task.setSymbol(coinAccount.getSellDecision());
      task.setTaskType(CoinTaskType.SELL);
      task.setTaskStatus(CoinTaskStatus.INIT);

      BigDecimal quantity = null;
      if (i == splitSize - 1) {
        BigDecimal planedQuantity = strategy.getQuotaPerSell().multiply(new BigDecimal(splitSize - 1));
        quantity = availableQuantity.subtract(planedQuantity);
      } else {
        quantity = strategy.getQuotaPerSell();
      }
      task.setQuantity(quantity);

      DateTime executeTime = DateTime.now().plusSeconds(i * strategy.getPerSellIntervalSeconds());
      task.setExecuteTime(executeTime);

      // 保存任务
      task = taskRepository.save(task);
      log.info("生成sell任务, {}", task);
    }

    // 更新当前账户中的预冻结数量
    coinAccount.setPreFreezeAmount(assetInfo.getAvailableAmount());
    coinAccountRepository.save(coinAccount);
  }
}