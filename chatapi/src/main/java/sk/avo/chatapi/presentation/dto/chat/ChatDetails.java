package sk.avo.chatapi.presentation.dto.chat;

import lombok.Data;
import java.util.Set;

/**
 * If the chatId is negative, it means that the chat is a group chat, otherwise the chat is private.
 */
@Data
public class ChatDetails {
    private Long chatId;
    private String name;
}
