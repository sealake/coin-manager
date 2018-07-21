package net.sealake.coin.service.task;

import net.sealake.coin.entity.CoinTask;
import net.sealake.coin.service.ExchangeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 已经提交的订单状态跟踪
 */
@Service
public class SellTaskTracker {

  @Autowired
  private ExchangeService exchangeService;

  @Async("trackExecutor")
  public void trackTaskStatus(CoinTask coinTask) {
    exchangeService.updateSellOrderStatus(coinTask);
  }
}