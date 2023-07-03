package sk.avo.chatapi.domain.user.exceptions;

public class UserEmailIsAlreadyVerifiedException extends BaseUserException {
    public UserEmailIsAlreadyVerifiedException(String message) {
        super(message);
    }
    public UserEmailIsAlreadyVerifiedException() {
        super("Email is already verified.");
    }
}
