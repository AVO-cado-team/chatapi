package sk.avo.chatapi.application.exceptions;

public class BaseApplicationException extends Exception {
    public BaseApplicationException() {
        super();
    }

    public BaseApplicationException(String message) {
        super(message);
    }

    public BaseApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseApplicationException(Throwable cause) {
        super(cause);
    }
}
