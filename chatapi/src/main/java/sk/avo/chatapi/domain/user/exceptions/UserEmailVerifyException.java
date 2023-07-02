package sk.avo.chatapi.domain.user.exceptions;

public class UserEmailVerifyException extends BaseUserException {
    public UserEmailVerifyException(String message) {
        super(message);
    }
    public UserEmailVerifyException() {
        super("Email verification failed.");
    }
}
