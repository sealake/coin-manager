package net.sealake.coin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.sealake.coin.entity.enums.BourceEnum;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * 交易所账号
 * @author melody
 * @version $Id: BourseAccount.java, v 0.1 2018年07月14日 下午8:08 melody Exp $
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class BourseAccount {

  @Id
  @GeneratedValue
  private Long id;

  @Column(length = 64)
  private String name;

  @Enumerated(value = EnumType.STRING)
  private BourceEnum bourceEnum;

  private String apiKey;

  private String secretKey;

  private String description;

  @OneToMany(mappedBy="bourseAccount", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  private List<CoinAccount> coinAccounts = new ArrayList<>();
}