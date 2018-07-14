package net.sealake.coin.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorMessage {

  private long timestamp;
  private int code;
  private String data;
  private String message;

  public ErrorMessage(final int code, final String message) {
    this.code = code;
    this.message = message;
    timestamp = System.currentTimeMillis();
  }

  public ErrorMessage(final int code, final String message, final String data) {
    this.code = code;
    this.message = message;
    this.data = data;
    timestamp = System.currentTimeMillis();
  }
}
