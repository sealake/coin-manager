package net.sealake.coin.api.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.sealake.coin.entity.BourseAccount;
import net.sealake.coin.entity.CoinSellStrategy;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * coin账号创建请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinAccountCreateRequest {

  @ApiModelProperty(value = "coin名称，如XZC BTC等")
  private String name;

  @ApiModelProperty(value = "coin地址")
  private String address;

  private String description;

  @ApiModelProperty(value = "卖出交易方向，如果支持与 USDT 交易，建议设置为 ${XXC}USDT，不支持的建议设置为${XXC}BTC")
  private String sellDecision;

  @ApiModelProperty(value = "买入交易方向, 应该为sellDecison的反方向，暂时不用")
  private String buyDecision;
}