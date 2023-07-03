package sk.avo.chatapi.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.avo.chatapi.domain.chat.models.ChatModel;

public interface IChatRepo extends JpaRepository<ChatModel, Long> {
}
