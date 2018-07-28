package net.sealake.coin.entity.enums;


import net.sealake.coin.entity.CoinTask;

public enum CoinTaskStatus {
  INIT,  // 任务初始化
  PROCESSING,  // 任务处理中，未提交订单
  COMMIT,      // 任务处理中，已向交易所提交订单
  SUCCESS, // 任务成功或失败（结束）
  FAIL
  ;

  public static boolean isSuccess(CoinTaskStatus status) {
    return status == null ? false : SUCCESS.equals(status);
  }

  public static boolean isFail(CoinTaskStatus status) {
    return status == null ? false: FAIL.equals(status);
  }

  public static boolean isFinished(CoinTaskStatus status) {
    return isSuccess(status) || isFail(status);
  }
}