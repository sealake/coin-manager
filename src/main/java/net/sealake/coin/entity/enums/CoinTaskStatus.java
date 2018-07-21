package net.sealake.coin.entity.enums;


public enum CoinTaskStatus {
  INIT,  // 任务初始化
  PROCESSING,  // 任务处理中，未提交订单
  COMMIT,      // 任务处理中，已向交易所提交订单
  SUCCESS, // 任务成功或失败（结束）
  FAIL
}