package net.sealake.coin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.sealake.coin.entity.enums.BoursePlatform;
import net.sealake.coin.entity.enums.CoinTaskStatus;
import net.sealake.coin.entity.enums.CoinTaskType;

import org.joda.time.DateTime;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CoinOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  // 账单流水号
  @Column(length = 64)
  private String bsnId;

  private Long bourseId;

  private Long coinId;

  @Column(unique = true)
  private Long taskId;

  @Enumerated(value = EnumType.STRING)
  private BoursePlatform platform;

  @Column(length = 32)
  private String symbol;

  @Column(precision = 20, scale = 12)
  private BigDecimal quantity;

  @Column(precision = 20, scale = 12)
  private BigDecimal price;

  @Enumerated(EnumType.STRING)
  private CoinTaskType taskType;

  @Enumerated(EnumType.STRING)
  private CoinTaskStatus taskStatus;

  private DateTime exchangeTime;
}
