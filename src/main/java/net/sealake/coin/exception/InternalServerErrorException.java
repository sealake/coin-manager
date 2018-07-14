package net.sealake.coin.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import net.sealake.coin.constants.AppError;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class InternalServerErrorException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private int errorCode;
  private String message;

  public InternalServerErrorException(final String message) {
    super();
    this.errorCode = AppError.OTHER_SERVER_INERNAL_EXCEPTION.getErrorCode();
    this.message = message;
  }

  ;

  public InternalServerErrorException(final AppError appError) {
    super();
    this.errorCode = appError.getErrorCode();
    this.message = appError.getMessageKey();
  }
}
