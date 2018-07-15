package net.sealake.coin.service.integration;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.api.response.ConnectionTestResponse;
import net.sealake.coin.constants.AppError;
import net.sealake.coin.entity.BourseAccount;
import net.sealake.coin.exception.InternalServerErrorException;
import net.sealake.coin.exception.NotFoundException;
import net.sealake.coin.repository.BourseAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExchangeService {
  @Autowired
  @Qualifier("binanceApiClient")
  private BinanceApiClient binanceApiClient;

  @Autowired
  private BourseAccountRepository bourseAccountRepository;

  public ConnectionTestResponse testConnection(final Long id) {
    BourseAccount bourseAccount = bourseAccountRepository.findOne(id);
    if (bourseAccount == null) {
      log.error("根据id获取交易所账户失败，账户不存在 id {}", id);
      throw new NotFoundException(AppError.DOCUMENT_NOT_FOUND);
    }

    BaseApiClient apiClient = getApiClient(bourseAccount);
    if (apiClient == null) {
      log.error("对应账户的API尚未支持, bourseAccount: {}", bourseAccount);
      throw new InternalServerErrorException(AppError.OTHER_CHANNEL_API_NOT_SUPPORT);
    }

    ConnectionTestResponse response = new ConnectionTestResponse();
    response.setSuccess(apiClient.testConnection());
    return response;
  }

  private BaseApiClient getApiClient(BourseAccount bourseAccount) {
    switch (bourseAccount.getBourseEnum()) {
      case BINANCE:
        return binanceApiClient;

      default:
        return null;
    }
  }
}