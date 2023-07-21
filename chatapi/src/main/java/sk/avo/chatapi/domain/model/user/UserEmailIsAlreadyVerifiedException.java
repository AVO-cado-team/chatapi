package sk.avo.chatapi.domain.model.user;

public class UserEmailIsAlreadyVerifiedException extends BaseUserException {
  public UserEmailIsAlreadyVerifiedException() {
    super("Email is already verified.");
  }
}
