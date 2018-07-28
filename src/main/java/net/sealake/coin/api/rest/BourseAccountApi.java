package net.sealake.coin.api.rest;

import com.jcabi.aspects.Loggable;

import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.api.request.BourseAccountCreateRequest;
import net.sealake.coin.api.response.ConnectionTestResponse;
import net.sealake.coin.constants.ApiConstants;
import net.sealake.coin.constants.AppError;
import net.sealake.coin.constants.Authorizes;
import net.sealake.coin.entity.BourseAccount;
import net.sealake.coin.entity.enums.UserStatusEnum;
import net.sealake.coin.exception.BadRequestException;
import net.sealake.coin.service.BourseAccountService;
import net.sealake.coin.service.integration.ApiClientManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 交易所账号管理api
 */
@Slf4j
@Loggable
@RestController
@RequestMapping(ApiConstants.API_V1)
public class BourseAccountApi {

  @Autowired
  private BourseAccountService bourseAccountService;

  @Autowired
  private ApiClientManager apiClientManager;

  @PostMapping("/bourses")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "创建交易所账户")
  public ResponseEntity<BourseAccount> createBourseAccount(@RequestBody BourseAccountCreateRequest request) {
    BourseAccount bourseAccount = bourseAccountService.createBourseAccount(request);
    return new ResponseEntity<>(bourseAccount, HttpStatus.CREATED);
  }

  @GetMapping("/bourses/{id}/connecttest")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "测试交易所账户连通性", notes = "如果不能连通，说明ak sk设置有问题，如果返回500，则对应渠道的api尚未提供支持")
  public ConnectionTestResponse testConnection(@PathVariable final Long id) {
    if (id == null) {
      log.error("交易所账户ID未提供!");
      throw new BadRequestException(AppError.BAD_REQUEST_INPUT_PARAMETER_INVALID);
    }

    return apiClientManager.testConnection(id);
  }

  @GetMapping("/bourses")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "查看所有交易所账户")
  public Page<BourseAccount> listBourseAccounts(@RequestParam final Integer page,
      @RequestParam final Integer size, @RequestParam final Sort.Direction sort) {

    int pageNumber = (page == null) ? 0 : page;
    int pageSize = (size == null) ? 10 : size;

    return bourseAccountService.listBourseAccounts(pageNumber, pageSize, sort);
  }

  @GetMapping("/bourses/{id}")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "交易所账户详情")
  public BourseAccount getBourseAccount(@PathVariable final Long id) {

    if (id == null) {
      log.error("bourseAccountId is null!");
      throw new BadRequestException(AppError.BAD_REQUEST_INPUT_PARAMETER_INVALID);
    }

    return bourseAccountService.getBourseAccount(id);
  }

  @DeleteMapping("/bourses/{id}")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "删除交易所账户, 该账户下的所有coin账号都会被级联删除")
  public ResponseEntity<String> deleteBourseAccount(@PathVariable final Long id) {
    if (id == null) {
      log.error("bourseAccountId is null!");
      throw new BadRequestException(AppError.BAD_REQUEST_INPUT_PARAMETER_INVALID);
    }

    bourseAccountService.deleteBourseAccount(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping("/bourse/{id}/status/{status}")
  @PreAuthorize(Authorizes.ADMIN)
  @ApiOperation(value = "更新用户状态（激活、非激活）")
  public BourseAccount updateUserStatus(@PathVariable("id") final Long id,
      @PathVariable("status") final String status) {
    UserStatusEnum statusEnum = UserStatusEnum.getByCode(status);
    if (status == null) {
      throw new BadRequestException(AppError.BAD_REQUEST_INPUT_PARAMETER_INVALID);
    }

    return bourseAccountService.updateBourseAccountStatus(id, statusEnum);
  }
}