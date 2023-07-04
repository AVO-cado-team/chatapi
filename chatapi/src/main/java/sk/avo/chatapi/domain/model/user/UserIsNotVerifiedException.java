package sk.avo.chatapi.domain.model.user;

public class UserIsNotVerifiedException extends BaseUserException {
  public UserIsNotVerifiedException() {
    super("User is not verified");
  }
}
