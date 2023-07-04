package sk.avo.chatapi.domain.model.security;

public class InvalidTokenException extends BaseSecurityException {
  public InvalidTokenException(String message) {
    super(message);
  }

  public InvalidTokenException() {
    super("Invalid token");
  }
}
