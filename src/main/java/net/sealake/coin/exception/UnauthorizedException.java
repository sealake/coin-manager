package net.sealake.coin.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import net.sealake.coin.constants.AppError;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UnauthorizedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private int errorCode;
  private String message;
  private String data;

  public UnauthorizedException(final AppError appError) {
    super();
    this.errorCode = appError.getErrorCode();
    this.message = appError.getMessageKey();
  }

  public UnauthorizedException(final AppError appError, final String data) {
    super();
    this.errorCode = appError.getErrorCode();
    this.message = appError.getMessageKey();
    this.data = data;
  }
}
