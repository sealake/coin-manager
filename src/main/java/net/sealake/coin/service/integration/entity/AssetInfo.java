package net.sealake.coin.service.integration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.sealake.coin.util.Json;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetInfo {

  // 资产名称（数字货币的名称）
  private String assetName;

  // 可交易份额
  private BigDecimal availableAmount;

  // 锁定中份额，即处理交易处理中的份额
  private BigDecimal freezeAmount;

  @Override
  public String toString() {
    return Json.dumps(this);
  }
}