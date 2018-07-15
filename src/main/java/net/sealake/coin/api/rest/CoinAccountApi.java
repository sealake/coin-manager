package net.sealake.coin.api.rest;

import com.jcabi.aspects.Loggable;

import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.api.request.CoinAccountCreateRequest;
import net.sealake.coin.constants.ApiConstants;
import net.sealake.coin.constants.AppError;
import net.sealake.coin.constants.Authorizes;
import net.sealake.coin.entity.CoinAccount;
import net.sealake.coin.entity.CoinSellStrategy;
import net.sealake.coin.exception.BadRequestException;
import net.sealake.coin.service.CoinAccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * coin账户管理接口
 */
@Slf4j
@Loggable
@RestController
@RequestMapping(ApiConstants.API_V1)
public class CoinAccountApi {

  @Autowired
  private CoinAccountService coinAccountService;

  @PostMapping("/bourses/{bourseId}/coins")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "创建coin账号")
  public ResponseEntity<CoinAccount> createCoinAccount(@PathVariable(name = "bourseId") final Long bourseId,
      @RequestBody CoinAccountCreateRequest request) {

    if (bourseId == null) {
      log.error("bourseId is null");
      throw new BadRequestException(AppError.BAD_REQUEST_INPUT_PARAMETER_INVALID);
    }

    CoinAccount coinAccount = coinAccountService.createCoinAccount(bourseId, request);
    return new ResponseEntity<>(coinAccount, HttpStatus.CREATED);
  }

  @GetMapping("/bourses/coins/{coinId}")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "查看coin账号详情")
  public CoinAccount getCoinAccount(@PathVariable final Long coinId) {

    if (coinId == null) {
      log.error("coinId is null");
      throw new BadRequestException(AppError.BAD_REQUEST_INPUT_PARAMETER_INVALID);
    }

    return coinAccountService.getCoinAccount(coinId);
  }

  @DeleteMapping("/bourses/coins/{coinId}")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "删除coin账号")
  public ResponseEntity<String> deleteCoinAccount(@PathVariable(name = "coinId") final Long coinId) {

    if (coinId == null) {
      log.error("coinId is null");
      throw new BadRequestException(AppError.BAD_REQUEST_INPUT_PARAMETER_INVALID);
    }

    coinAccountService.deleteCoinAccount(coinId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}