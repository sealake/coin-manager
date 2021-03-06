package net.sealake.coin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.sealake.coin.entity.enums.BoursePlatform;
import net.sealake.coin.entity.enums.CoinTaskStatus;
import net.sealake.coin.entity.enums.CoinTaskType;
import net.sealake.coin.util.Json;

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
public class CoinTask {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Long bourseId;

  @Enumerated(EnumType.STRING)
  private BoursePlatform platform;

  private Long coinId;

  private String symbol;

  @Column(precision = 20, scale = 12)
  private BigDecimal quantity;

  private DateTime executeTime;

  @Enumerated(EnumType.STRING)
  private CoinTaskStatus taskStatus;

  @Enumerated(EnumType.STRING)
  private CoinTaskType taskType;

  // 业务流水号
  private String bsnId;

  @Override
  public String toString() {
    return Json.dumps(this);
  }
}