package net.sealake.coin.api.request;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import javax.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinSellStrategyRequest {
  @ApiModelProperty(value = "每次卖出的限额")
  private BigDecimal quotaPerSell;

  @ApiModelProperty(value = "超出限额后，拆分之后的交易时间间隔")
  private int perSellIntervalSeconds;
}