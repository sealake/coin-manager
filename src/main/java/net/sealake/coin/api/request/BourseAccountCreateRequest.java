package net.sealake.coin.api.request;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.sealake.coin.entity.enums.BourceEnum;

/**
 * 交易所账号创建请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BourseAccountCreateRequest {

  @ApiModelProperty(value = "平台名称")
  private String name;

  @ApiModelProperty(value = "支持的交易所平台列表")
  private BourceEnum bourceEnum;

  private String apiKey;

  private String secretKey;

  private String description;
}