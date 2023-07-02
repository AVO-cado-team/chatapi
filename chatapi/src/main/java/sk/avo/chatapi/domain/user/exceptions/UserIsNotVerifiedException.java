package sk.avo.chatapi.domain.user.exceptions;

public class UserIsNotVerifiedException extends BaseUserException {
    public UserIsNotVerifiedException(String message) {
        super(message);
    }
    public UserIsNotVerifiedException() {
        super("User is not verified");
    }
}
