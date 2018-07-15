package net.sealake.coin.service;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.api.request.CoinAccountCreateRequest;
import net.sealake.coin.constants.AppError;
import net.sealake.coin.entity.BourseAccount;
import net.sealake.coin.entity.CoinAccount;
import net.sealake.coin.exception.BadRequestException;
import net.sealake.coin.exception.NotFoundException;
import net.sealake.coin.repository.BourseAccountRepository;
import net.sealake.coin.repository.CoinAccountRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
public class CoinAccountService {

  @Autowired
  private CoinAccountRepository coinAccountRepository;

  @Autowired
  private BourseAccountRepository bourseAccountRepository;

  public CoinAccount createCoinAccount(final Long bourseId, final CoinAccountCreateRequest request) {

    final BourseAccount bourseAccount = bourseAccountRepository.findOne(bourseId);
    if (bourseAccount == null) {
      log.error("failed get bourseAccount, id {}", bourseId);
      throw new BadRequestException(AppError.BAD_REQUEST_INPUT_PARAMETER_INVALID);
    }

    CoinAccount coinAccount = new CoinAccount();
    BeanUtils.copyProperties(request, coinAccount);
    coinAccount.setBourseAccount(bourseAccount);

    return coinAccountRepository.save(coinAccount);
  }

  public CoinAccount getCoinAccount(final Long coinId) {
    final CoinAccount coinAccount = coinAccountRepository.findOne(coinId);
    if (coinAccount == null) {
      log.error("根据id获取coin账号失败，id：{}", coinId);
      throw new NotFoundException(AppError.DOCUMENT_NOT_FOUND);
    }

    return coinAccount;
  }

  public void deleteCoinAccount(final Long id) {
    coinAccountRepository.delete(id);
  }
}