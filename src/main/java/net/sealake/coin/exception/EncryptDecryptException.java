package net.sealake.coin.exception;

public class EncryptDecryptException extends RuntimeException {
  private static final long serialVersionUID = -1515359992811684444L;

  public EncryptDecryptException() {
    super();
  }

  public EncryptDecryptException(String msg) {
    super(msg);
  }

  public EncryptDecryptException(String msg, Throwable t) {
    super(msg, t);
  }

  public EncryptDecryptException(Throwable t) {
    super(t);
  }

}
