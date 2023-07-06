package sk.avo.chatapi.domain.model.chat;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class MessageId implements Serializable {
    private Long chatId;
    private Long messageId;
}
