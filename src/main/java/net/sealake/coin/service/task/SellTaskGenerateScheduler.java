package net.sealake.coin.service.task;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.entity.BourseAccount;
import net.sealake.coin.entity.CoinAccount;
import net.sealake.coin.service.integration.ApiClientManager;
import net.sealake.coin.service.integration.BaseApiClient;
import net.sealake.coin.service.integration.BourseCoinConfigLoader;
import net.sealake.coin.service.integration.entity.AccountInfo;
import net.sealake.coin.service.integration.entity.AssetInfo;
import net.sealake.coin.util.Json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * sell异步任务生成定时调度器
 */
@Slf4j
@Service
public class SellTaskGenerateScheduler {

  // 暂定为10min
  private static final long FIXED_DELAY = 10 * 60 * 1000L;

  @Autowired
  private ApiClientManager apiClientManager;

  @Autowired
  private BourseCoinConfigLoader bourseCoinConfigLoader;

  @Autowired
  private SellTaskGenerator sellTaskGenerator;

  @Scheduled(fixedDelay = FIXED_DELAY)
  public void generateSellTask() {
    // 从数据库中取出所有交易所账户 && coin 账户配置
    bourseCoinConfigLoader.loadConfigs();

    log.info("current config: {}", Json.dumps(bourseCoinConfigLoader.getBourseAccountMap()));

    // 遍历所有的交易所账户，生成sell 任务
    for (Map.Entry<String, BourseAccount> entry: bourseCoinConfigLoader.getBourseAccountMap().entrySet()) {
      String bourseName = entry.getKey();
      BourseAccount bourseAccount = entry.getValue();

      // 如果该账户未激活
      if (!bourseAccount.isActive()) {
        log.warn("当前交易所账户未激活，id {}, name {} ak {}",
            bourseAccount.getId(), bourseAccount.getName(), bourseAccount.getApiKey());
        continue;
      }

      // 获取交易所api client, 判断交易所能否联通
      BaseApiClient apiClient = apiClientManager.getApiClient(bourseAccount);
      if (apiClient == null) {
        log.warn("交易所账户client实例获取失败，bourseAccount id {}, name {}, ak: {}",
            bourseAccount.getId(), bourseAccount.getName(), bourseAccount.getApiKey());
        continue;
      }

      // 获取账户资产信息，判断账户资产是否足以交易
      AccountInfo accountInfo = apiClient.getAccountInfo();
      if (accountInfo == null || !accountInfo.isCanTrade()) {
        continue;
      }

      log.info("bourseAccount can trade，id {}, name {}, ak {}",
          bourseAccount.getId(), bourseAccount.getName(), bourseAccount.getApiKey());

      // 获取该账户下coin账户信息，如果coin-manager端支持该种类coin交易，则创建交易异步任务
      Map<String, CoinAccount> coinAccountMap = bourseCoinConfigLoader.getBourseCoinAccountMap().get(bourseName);
      for (AssetInfo assetInfo: accountInfo.getAssetInfos()) {
        CoinAccount coinAccount = coinAccountMap.get(assetInfo.getAssetName());
        if (coinAccount != null) {
          if (assetInfo.getAvailableAmount().compareTo(BigDecimal.ZERO) > 0) {
            sellTaskGenerator.generatorTask(bourseAccount, coinAccount.getId(), assetInfo);
          } else if (assetInfo.getAvailableAmount().compareTo(BigDecimal.ZERO) < 0) {
            log.error("当前coin账户预冻结金额小于0，请核对历史订单! coinAccount: {}", coinAccount);
          }
        }
      }
    }
  }
}