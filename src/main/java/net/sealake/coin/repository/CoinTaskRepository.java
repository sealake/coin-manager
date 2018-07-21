package net.sealake.coin.repository;

import net.sealake.coin.entity.CoinTask;
import net.sealake.coin.entity.enums.CoinTaskStatus;
import net.sealake.coin.entity.enums.CoinTaskType;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.persistence.LockModeType;

import retrofit2.http.PATCH;

@Repository
public interface CoinTaskRepository extends JpaRepository<CoinTask, Long> {

  /**
   * 根据任务类型、运行状态查询执行时间早于指定时间的tasks
   */
  @Lock(value = LockModeType.PESSIMISTIC_WRITE)
  @Query(value = "select t from CoinTask t " +
      "where t.taskType = :taskType " +
      "and t.taskStatus = :taskStatus " +
      "and t.executeTime <= :curTime")
  Page<CoinTask> loadTask(@Param("taskType") CoinTaskType taskType,
      @Param("taskStatus") CoinTaskStatus taskStatus,
      @Param("curTime") DateTime curTime, Pageable pageable);

  /**
   * 根据任务类型、运行状态查询tasks
   */
  @Query(value = "select t from CoinTask t " +
      "where t.taskType = :taskType " +
      "and t.taskStatus = :taskStatus ")
  List<CoinTask> loadTask(@Param("taskType") CoinTaskType taskType,
      @Param("taskStatus") CoinTaskStatus taskStatus);
}