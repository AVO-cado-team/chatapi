package sk.avo.chatapi.domain.model.user;

public class BaseUserException extends Exception {
  public BaseUserException() {
    super();
  }

  public BaseUserException(String message) {
    super(message);
  }

  public BaseUserException(String message, Throwable cause) {
    super(message, cause);
  }

  public BaseUserException(Throwable cause) {
    super(cause);
  }
}
