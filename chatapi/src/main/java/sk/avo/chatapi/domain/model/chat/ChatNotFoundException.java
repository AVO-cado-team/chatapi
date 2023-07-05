package sk.avo.chatapi.domain.model.chat;

public class ChatNotFoundException extends BaseChatException {
    public ChatNotFoundException() {
        super("Chat not found");
    }
}
