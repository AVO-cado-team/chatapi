package sk.avo.chatapi.domain.model.chat;

public class UserIsAlreadyInTheChatException extends BaseChatException {
    public UserIsAlreadyInTheChatException() {
        super("User is already in the chat");
    }
}
