package net.sealake.coin.service.integration;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.api.response.ConnectionTestResponse;
import net.sealake.coin.constants.AppError;
import net.sealake.coin.entity.BourseAccount;
import net.sealake.coin.exception.InternalServerErrorException;
import net.sealake.coin.exception.NotFoundException;
import net.sealake.coin.repository.BourseAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApiClientManager {

  @Autowired
  private BourseAccountRepository bourseAccountRepository;

  /**
   * 交易所远程服务连通性测试
   */
  public ConnectionTestResponse testConnection(final Long id) {
    final ConnectionTestResponse response = new ConnectionTestResponse();
    response.setSuccess(true);

    // 根据id获取交易所账户
    BourseAccount bourseAccount = bourseAccountRepository.findOne(id);
    if (bourseAccount == null) {
      log.error("根据id获取交易所账户失败，账户不存在 id {}", id);
      throw new NotFoundException(AppError.DOCUMENT_NOT_FOUND);
    }

    // 根据交易所账户生成对应的client实例，并测试远程服务连通性
    BaseApiClient apiClient = getApiClient(bourseAccount);
    if (apiClient == null) {
      log.error("ak、sk设置错误，或对应账户的API尚未支持, bourseAccount: {}", bourseAccount);
      response.setSuccess(false);
    }

    return response;
  }

  /**
   * 根据 bourseAccount 配置创建客户端
   */
  public BaseApiClient getApiClient(BourseAccount bourseAccount) {
    switch (bourseAccount.getPlatform()) {
      case BINANCE:
        BinanceApiClient binanceApiClient = new BinanceApiClient();
        binanceApiClient.reloadClient(bourseAccount.getApiKey(), bourseAccount.getSecretKey());
        if (binanceApiClient.testConnection()) {
          return binanceApiClient;
        }
        return null;

      case CRYPTOPIA:
        CryptopiaApiClient cryptopiaApiClient = new CryptopiaApiClient();
        cryptopiaApiClient.reloadClient(bourseAccount.getApiKey(), bourseAccount.getSecretKey());
        if (cryptopiaApiClient.testConnection()) {
          return cryptopiaApiClient;
        }
        return null;

      case BITTREX:
        BittrexApiClient bittrexApiClient = new BittrexApiClient();
        bittrexApiClient.reloadClient(bourseAccount.getApiKey(), bourseAccount.getSecretKey());
        if (bittrexApiClient.testConnection()) {
          return bittrexApiClient;
        }
        return null;

      default:
        return null;
    }
  }
}