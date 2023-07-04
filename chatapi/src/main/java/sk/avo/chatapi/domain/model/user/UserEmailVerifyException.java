package sk.avo.chatapi.domain.model.user;

public class UserEmailVerifyException extends BaseUserException {
    public UserEmailVerifyException() {
        super("Email verification failed.");
    }
}
