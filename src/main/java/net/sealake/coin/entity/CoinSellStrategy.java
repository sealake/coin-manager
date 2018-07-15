package net.sealake.coin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * coin卖出策略
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CoinSellStrategy {
  @Id
  @GeneratedValue
  private Long id;

  @Column(precision = 20, scale = 12)
  @ApiModelProperty(value = "每次卖出的限额")
  private BigDecimal quotaPerSell;

  @ApiModelProperty(value = "超出限额后，拆分之后的交易时间间隔")
  private int perSellIntervalSeconds;

  /** 关联coin账号 */
  @JsonIgnore
  @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "coin_account_id")
  private CoinAccount coinAccount;
}