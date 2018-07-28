package net.sealake.coin.api.request;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.sealake.coin.entity.enums.BoursePlatform;
import net.sealake.coin.entity.enums.UserStatusEnum;

/**
 * 交易所账号创建请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BourseAccountCreateRequest {

  @ApiModelProperty(value = "平台名称")
  private String name;

  @ApiModelProperty(value = "交易所平台")
  private BoursePlatform platform;

  private UserStatusEnum status;

  private String apiKey;

  private String secretKey;

  private String description;
}