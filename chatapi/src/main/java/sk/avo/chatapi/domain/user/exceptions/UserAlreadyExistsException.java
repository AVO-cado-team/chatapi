package sk.avo.chatapi.domain.user.exceptions;

public class UserAlreadyExistsException extends BaseUserException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    public UserAlreadyExistsException() {
        super("User already exists");
    }
}
