package sk.avo.chatapi.domain.model.chat;

public class MessageNotFoundException extends BaseChatException {
  public MessageNotFoundException() {
    super("Message not found");
  }
}
