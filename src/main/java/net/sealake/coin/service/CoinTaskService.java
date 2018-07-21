package net.sealake.coin.service;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.entity.CoinTask;
import net.sealake.coin.entity.enums.CoinTaskStatus;
import net.sealake.coin.entity.enums.CoinTaskType;
import net.sealake.coin.repository.CoinTaskRepository;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * coin task 数据管理
 */
@Slf4j
@Service
public class CoinTaskService {

  private static final int START_PAGE_INDEX = 0;

  @Autowired
  private CoinTaskRepository coinTaskRepository;

  /**
   * load未运行的异步命令，将异步任务置成 PROCESSING
   * @param taskType
   * @return
   */
  @Transactional
  public List<CoinTask> loadInitTask(CoinTaskType taskType, int loadSize) {
    final List<CoinTask> results = new ArrayList<>();

    // 查询分页并锁表
    Pageable pageable=new PageRequest(START_PAGE_INDEX, loadSize);
    Page<CoinTask> taskPage = coinTaskRepository.loadTask(taskType, CoinTaskStatus.INIT, DateTime.now(), pageable);
    for (CoinTask task: taskPage.getContent()) {

      // 更新状态为PROCESSING，保证下一次不会load到这些数据
      task.setTaskStatus(CoinTaskStatus.PROCESSING);
      results.add(coinTaskRepository.save(task));
    }

    return results;
  }

  /**
   * 捞取已经提交订单的异步命令。用于定时查看订单状态。
   * @param taskType
   * @return
   */
  @Transactional
  public List<CoinTask> loadCommitedTask(CoinTaskType taskType) {

    // 查询分页并锁表
    // Pageable pageable=new PageRequest(START_PAGE_INDEX, loadSize);

    return coinTaskRepository.loadTask(taskType, CoinTaskStatus.COMMIT);
  }
}
