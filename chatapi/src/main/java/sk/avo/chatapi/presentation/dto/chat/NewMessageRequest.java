package sk.avo.chatapi.presentation.dto.chat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data public class NewMessageRequest {
  @NotNull
  private String text;
  @NotNull
  private String messageType;
  private Long replyTo;
}
