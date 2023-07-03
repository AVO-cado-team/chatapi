package sk.avo.chatapi.domain.security.exceptions;

public class BaseSecurityException extends RuntimeException {
    public BaseSecurityException(String message) {
        super(message);
    }
}
