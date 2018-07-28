package net.sealake.coin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.sealake.coin.util.Json;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * Coin账号
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CoinAccount {

  @Id
  @GeneratedValue
  private Long id;

  @ApiModelProperty(value = "coin名称，如XZC BTC等")
  private String name;

  @ApiModelProperty(value = "coin地址")
  private String address;

  /**
   * 当前预冻结的资金份额
   */
  @Column(precision = 20, scale = 12)
  @ApiModelProperty(value = "当前预冻结的资金份额，即分批次尚未执行卖出的资金份额。正在执行中的为下单冻结，本地不记录")
  private BigDecimal preFreezeAmount = new BigDecimal("0");

  private String description;

  @ApiModelProperty(value = "卖出交易方向，如果支持与 USDT 交易，建议设置为 USDT，不支持的建议设置为 BTC")
  private String sellDecision;

  @ApiModelProperty(value = "买入交易方向, 应该为sellDecison的反方向，暂时不用")
  private String buyDecision;

  /** 所属交易所账号 */
  @JsonIgnore
  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "bourse_account_id")
  private BourseAccount bourseAccount;

  /** 货币卖出策略 */
  @OneToOne(mappedBy="coinAccount", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  private CoinSellStrategy coinSellStrategy;

  @Override
  public String toString() {
    return Json.dumps(this);
  }
}

