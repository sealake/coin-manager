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
public class NotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private int errorCode;
  private String message;

  public NotFoundException(final String message) {
    super();
    this.errorCode = AppError.DOCUMENT_NOT_FOUND.getErrorCode();
    this.message = message;
  }

  public NotFoundException(final AppError appError) {
    super();
    this.errorCode = appError.getErrorCode();
    this.message = appError.getMessageKey();
  }
}
