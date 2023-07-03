package sk.avo.chatapi.domain.security.exceptions;

public class InvalidToken extends BaseSecurityException{
    public InvalidToken(String message) {
        super(message);
    }

    public InvalidToken() {
        super("Invalid token");
    }
}
