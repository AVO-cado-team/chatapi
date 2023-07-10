package sk.avo.chatapi.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ChatId {
  private Long value;

    public ChatId (String value){
        this.value = Long.valueOf(value);
    }
}
