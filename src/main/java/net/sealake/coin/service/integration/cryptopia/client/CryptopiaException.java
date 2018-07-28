package net.sealake.coin.service.integration.cryptopia.client;

public class CryptopiaException extends RuntimeException {

  public CryptopiaException(String message) {
    super(message);
  }

  public CryptopiaException(String message, Throwable cause) {
    super(message, cause);
  }
}