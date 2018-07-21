package net.sealake.coin.service.task;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.entity.CoinTask;
import net.sealake.coin.entity.enums.CoinTaskType;
import net.sealake.coin.service.CoinTaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SellTaskTrackScheduler {

  private static final int FIXED_INTERVAL_MILLS = 10000;

  @Autowired
  private SellTaskTracker sellTaskTracker;

  @Autowired
  private CoinTaskService taskService;

  @Scheduled(fixedRate = FIXED_INTERVAL_MILLS)
  public void trackTaskStatus() {
    List<CoinTask> tasks = taskService.loadCommitedTask(CoinTaskType.SELL);
    for (CoinTask task: tasks) {
      sellTaskTracker.trackTaskStatus(task);
    }
  }
}