package sk.avo.chatapi.domain.model.user;

public class UserNotFoundException extends BaseUserException {
  public UserNotFoundException() {
    super("User not found");
  }
}
