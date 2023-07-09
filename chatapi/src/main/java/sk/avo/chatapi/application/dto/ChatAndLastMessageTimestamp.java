package sk.avo.chatapi.application.dto;

import sk.avo.chatapi.domain.model.chat.ChatEntity;
import lombok.Data;
import java.util.Date;

@Data public class ChatAndLastMessageTimestamp {
  private ChatEntity chat;
  private Date lastMessageTimestamp;
}
