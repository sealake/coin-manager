package net.sealake.coin.service.integration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.entity.BourseAccount;
import net.sealake.coin.entity.CoinAccount;
import net.sealake.coin.repository.BourseAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定期自动从数据库获取最新的交易所 && coin配置
 * FIXME: 可以在BourseAccountService CoinAccountService CoinSellStrategyService加入切面，每次增删改之后立即刷新配置。
 */
@Slf4j
@Getter
@Service
public class BourseCoinConfigLoader {
  @Autowired
  private BourseAccountRepository bourseAccountRepository;

  /** key: BourseEnum */
  private Map<String, BourseAccount> bourseAccountMap;

  /** 外层key: BourseEnum, 内层key: coin name */
  private Map<String, Map<String, CoinAccount>> bourseCoinAccountMap;

  public void loadConfigs() {
    loadAllConfigs();
  }

  /**
   * 从数据库中取出所有的交易所、coin配置
   * 调用频率极低
   */
  private synchronized void loadAllConfigs() {
    if (CollectionUtils.isEmpty(bourseAccountMap)) {
      bourseAccountMap = new HashMap<String, BourseAccount>();
    }

    if (CollectionUtils.isEmpty(bourseCoinAccountMap)) {
      bourseCoinAccountMap = new HashMap<String, Map<String, CoinAccount>>();
    }

    final List<BourseAccount> bourseAccounts = bourseAccountRepository.findAll();
    for (BourseAccount bourseAccount : bourseAccounts) {
      // bourse account config map
      String bourseName = bourseAccount.getBourseEnum().name();
      bourseAccountMap.put(bourseName, bourseAccount);

      // bourse and coin accont config map
      Map<String, CoinAccount> coinAccountMap = new HashMap<>();
      for (CoinAccount coinAccount: bourseAccount.getCoinAccounts()) {
        coinAccountMap.put(coinAccount.getName(), coinAccount);
      }
      bourseCoinAccountMap.put(bourseAccount.getBourseEnum().name(), coinAccountMap);
    }
  }
}