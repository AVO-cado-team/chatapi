package sk.avo.chatapi.domain.model.user;

public class UserAlreadyExistsException extends BaseUserException {
  public UserAlreadyExistsException() {
    super("User already exists");
  }
}
