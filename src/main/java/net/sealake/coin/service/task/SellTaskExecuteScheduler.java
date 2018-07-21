package net.sealake.coin.service.task;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.entity.CoinTask;
import net.sealake.coin.entity.enums.CoinTaskType;
import net.sealake.coin.service.CoinTaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * 异步任务执行调度器
 */
@Slf4j
@Service
public class SellTaskExecuteScheduler {

  private static final long FIXED_INTERVAL_MILLS = 5000;  // 5 secs

  private static final int LOAD_SIZE_PERSCHE = 10;

  @Autowired
  private CoinTaskService coinTaskService;

  @Autowired
  private SellTaskExecutor sellTaskExecutor;

  // @Scheduled(fixedRate = FIXED_INTERVAL_MILLS)
  public void loadAndExecuteSellTask() {

    List<CoinTask> tasks = coinTaskService.loadInitTask(CoinTaskType.SELL, LOAD_SIZE_PERSCHE);
    for (CoinTask task: tasks) {
      sellTaskExecutor.execute(task);
    }
  }
}