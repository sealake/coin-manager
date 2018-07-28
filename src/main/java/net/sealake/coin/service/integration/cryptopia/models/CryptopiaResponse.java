package net.sealake.coin.service.integration.cryptopia.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.sealake.coin.service.integration.cryptopia.client.CryptopiaException;

@Data
@NoArgsConstructor
public class CryptopiaResponse<T> {
  private boolean success;
  private String message;
  private T data;
  private String json;

  public void validate() {
    if (success) {
      return;
    }
    throw new CryptopiaException(message);
  }
}
