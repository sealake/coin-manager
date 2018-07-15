package net.sealake.coin.service;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.api.request.CoinSellStrategyRequest;
import net.sealake.coin.constants.AppError;
import net.sealake.coin.entity.CoinAccount;
import net.sealake.coin.entity.CoinSellStrategy;
import net.sealake.coin.exception.BadRequestException;
import net.sealake.coin.exception.NotFoundException;
import net.sealake.coin.repository.CoinAccountRepository;
import net.sealake.coin.repository.CoinSellStrategyRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CoinSellStrategyService {
  @Autowired
  private CoinSellStrategyRepository sellStrategyRepository;

  @Autowired
  private CoinAccountRepository coinAccountRepository;

  /**
   * 创建coin卖出策略
   */
  public CoinSellStrategy createCoinSellStrategy(final Long coinId, final CoinSellStrategyRequest request) {
    CoinAccount coinAccount = coinAccountRepository.findOne(coinId);
    if (null == coinAccount) {
      log.error("传入参数错误! 根据coinId获取coin账户失败，coinId: {}", coinId);
      throw new BadRequestException(AppError.BAD_REQUEST_INPUT_PARAMETER_INVALID);
    }

    CoinSellStrategy coinSellStrategy = new CoinSellStrategy();
    BeanUtils.copyProperties(request, coinSellStrategy);
    coinSellStrategy.setCoinAccount(coinAccount);

    return sellStrategyRepository.save(coinSellStrategy);
  }

  /**
   * 根据id获取策略详情
   */
  public CoinSellStrategy getCoinSellStrategy(final Long strategyId) {
    final CoinSellStrategy sellStrategy = sellStrategyRepository.findOne(strategyId);
    if (sellStrategy == null) {
      log.error("根据id获取策略失败，id: {}", strategyId);
      throw new NotFoundException(AppError.DOCUMENT_NOT_FOUND);
    }

    return sellStrategy;
  }

  /**
   * 更新coin卖出策略
   */
  public CoinSellStrategy updateCoinSellStrategy(final Long strategyId, final CoinSellStrategyRequest request) {
    CoinSellStrategy sellStrategy = sellStrategyRepository.findOne(strategyId);
    if (null == sellStrategy) {
      log.error("sellStrategy不存在。根据id获取sellStrategy失败，id: {}", strategyId);
      throw new NotFoundException(AppError.DOCUMENT_NOT_FOUND);
    }

    sellStrategy.setQuotaPerSell(request.getQuotaPerSell());
    sellStrategy.setPerSellIntervalSeconds(request.getPerSellIntervalSeconds());
    return sellStrategyRepository.save(sellStrategy);
  }

  /**
   * 删除策略
   */
  public void deleteCoinSellStrategy(final Long strategyId) {
    sellStrategyRepository.delete(strategyId);
  }
}