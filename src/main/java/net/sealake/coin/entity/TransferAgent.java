package net.sealake.coin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * TA账户,对应每个交易所的数字货币售卖专户
 * @author melody
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferAgent {
  @Id
  @GeneratedValue
  private Long id;

  @Column(length = 32)
  private String taName;

  @Column(length = 64)
  private String accessKey;

  @Column(length = 64)
  private String secretKey;

  @Column(precision = 20, scale = 12)
  private BigDecimal currentAmount;

  @Column(precision = 20, scale = 12)
  private BigDecimal freezeAmount;
}
