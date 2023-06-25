package sk.avo.chatapi.domain.chat.exceptions;

public class BaseChatException extends Exception {
    public BaseChatException() {
        super();
    }

    public BaseChatException(String message) {
        super(message);
    }

    public BaseChatException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseChatException(Throwable cause) {
        super(cause);
    }
}
