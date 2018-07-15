package net.sealake.coin.api.rest;

import com.jcabi.aspects.Loggable;

import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.api.request.CoinSellStrategyRequest;
import net.sealake.coin.constants.ApiConstants;
import net.sealake.coin.constants.AppError;
import net.sealake.coin.constants.Authorizes;
import net.sealake.coin.entity.CoinSellStrategy;
import net.sealake.coin.exception.BadRequestException;
import net.sealake.coin.service.CoinSellStrategyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * coin卖出策略管理
 */
@Slf4j
@Loggable
@RestController
@RequestMapping(ApiConstants.API_V1)
public class CoinSellStrategyApi {

  @Autowired
  private CoinSellStrategyService sellStrategyService;

  @PostMapping("/coins/{coinId}/sellstrategys")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "创建卖出策略")
  public ResponseEntity<CoinSellStrategy> createSellStrategy(@PathVariable final Long coinId,
      @RequestBody CoinSellStrategyRequest request) {

    if (coinId == null) {
      log.error("策略所属coin账号Id为空");
      throw new BadRequestException(AppError.BAD_REQUEST_INPUT_PARAMETER_INVALID);
    }

    CoinSellStrategy sellStrategy = sellStrategyService.createCoinSellStrategy(coinId, request);
    return new ResponseEntity<>(sellStrategy, HttpStatus.CREATED);
  }

  @GetMapping("/coins/sellstrategys/{strategyId}")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "查看卖出策略详情")
  public CoinSellStrategy getSellStrategy(@PathVariable final Long strategyId) {

    checkStrategyId(strategyId);
    return sellStrategyService.getCoinSellStrategy(strategyId);
  }

  @PutMapping("/coins/sellstrategys/{strategyId}")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "调整卖出策略")
  public CoinSellStrategy updateSellStrategy(@PathVariable final Long strategyId,
      @RequestBody CoinSellStrategyRequest request) {

    checkStrategyId(strategyId);
    return sellStrategyService.updateCoinSellStrategy(strategyId, request);
  }

  @DeleteMapping("/coins/sellstrategys/{strategyId}")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "删除卖出策略")
  public ResponseEntity<String> deleteSellStrategy(@PathVariable final Long strategyId) {

    checkStrategyId(strategyId);
    sellStrategyService.deleteCoinSellStrategy(strategyId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  private void checkStrategyId(final Long strategyId) {
    if (strategyId == null) {
      log.error("策略id为空");
      throw new BadRequestException(AppError.BAD_REQUEST_INPUT_PARAMETER_INVALID);
    }
  }
}
