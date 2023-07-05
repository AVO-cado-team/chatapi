package sk.avo.chatapi.domain.model.chat;

public class UserIsNotInTheChatException extends BaseChatException {
    public UserIsNotInTheChatException() {
        super("User is not in the chat.");
    }
}
