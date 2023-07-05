package sk.avo.chatapi.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.avo.chatapi.domain.model.chat.ChatEntity;

public interface ChatRepo extends JpaRepository<ChatEntity, Long> {
}
