package sk.avo.chatapi.application.exceptions;

public class SubscriptionPathNotFoundException extends BaseApplicationException {

    public SubscriptionPathNotFoundException() {
        super();
    }

    public SubscriptionPathNotFoundException(String message) {
        super(message);
    }

    public SubscriptionPathNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubscriptionPathNotFoundException(Throwable cause) {
        super(cause);
    }
}
