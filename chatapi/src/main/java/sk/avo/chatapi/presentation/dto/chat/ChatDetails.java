package sk.avo.chatapi.presentation.dto.chat;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

import java.util.Set;

/**
 * If the chatId is negative, it means that the chat is a group chat, otherwise the chat is private.
 */

@Builder
@Data
public class ChatDetails {
    private Long chatId;
    private String name;
    private Long lastMessageTime;
    @Default
    private Set<ChatUser> users = Set.of();

    public static class ChatDetailsBuilder
    {
        public ChatDetailsBuilder() { }
    }
}
