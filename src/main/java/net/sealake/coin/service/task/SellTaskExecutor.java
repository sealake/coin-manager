package net.sealake.coin.service.task;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.entity.CoinTask;
import net.sealake.coin.service.ExchangeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/*
 * 卖出异步执行器
 */
@Slf4j
@Service
public class SellTaskExecutor {

  @Autowired
  private ExchangeService exchangeService;

  @Async("sellExecutor")
  public void execute(CoinTask task) {
    exchangeService.sell(task);
  }
}