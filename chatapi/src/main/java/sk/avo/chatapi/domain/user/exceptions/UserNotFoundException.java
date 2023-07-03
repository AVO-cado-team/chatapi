package sk.avo.chatapi.domain.user.exceptions;

public class UserNotFoundException extends BaseUserException{
    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException() {
        super("User not found");
    }
}