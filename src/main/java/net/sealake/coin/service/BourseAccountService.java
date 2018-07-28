package net.sealake.coin.service;

import lombok.extern.slf4j.Slf4j;

import net.sealake.binance.api.client.BinanceApiError;
import net.sealake.coin.api.request.BourseAccountCreateRequest;
import net.sealake.coin.constants.ApiConstants;
import net.sealake.coin.constants.AppError;
import net.sealake.coin.entity.BourseAccount;
import net.sealake.coin.entity.enums.UserStatusEnum;
import net.sealake.coin.exception.NotFoundException;
import net.sealake.coin.repository.BourseAccountRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BourseAccountService {

  @Autowired
  private BourseAccountRepository bourseAccountRepository;

  /**
   * 创建交易所账户
   * @param request 请求体
   * @return 创建的账户信息
   */
  public BourseAccount createBourseAccount(BourseAccountCreateRequest request) {

    BourseAccount bourseAccount = new BourseAccount();
    BeanUtils.copyProperties(request, bourseAccount);

    return bourseAccountRepository.save(bourseAccount);
  }

  /**
   * 分页查询所有的交易所账户信息
   */
  public Page<BourseAccount> listBourseAccounts(final Integer page, final Integer size, final Sort.Direction sort) {
    Pageable pageable = new PageRequest(page, size, sort, "id");
    return bourseAccountRepository.findAll(pageable);
  }

  public BourseAccount getBourseAccount(final Long id) {
    BourseAccount bourseAccount = bourseAccountRepository.findOne(id);
    if (bourseAccount == null) {
      log.error("根据id获取交易所账号失败，id：{}", id);
      throw new NotFoundException(AppError.DOCUMENT_NOT_FOUND);
    }

    return bourseAccount;
  }

  /**
   * 删除交易所账户
   * @param accountId 要删除的账户id
   */
  public void deleteBourseAccount(final Long accountId) {
    bourseAccountRepository.delete(accountId);
  }

  public BourseAccount updateBourseAccountStatus(final Long id, final UserStatusEnum status) {
    BourseAccount bourseAccount = bourseAccountRepository.findOne(id);
    if (bourseAccount == null) {
      throw new NotFoundException(AppError.DOCUMENT_NOT_FOUND);
    }

    if (bourseAccount.getStatus() == null || !bourseAccount.getStatus().equals(status)) {
      bourseAccount.setStatus(status);
      bourseAccount = bourseAccountRepository.save(bourseAccount);
    }

    return bourseAccount;
  }
}