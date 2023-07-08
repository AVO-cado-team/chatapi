package sk.avo.chatapi.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sk.avo.chatapi.domain.model.chat.ChatEntity;

import java.util.Set;

public interface ChatRepo extends JpaRepository<ChatEntity, Long> {
    @Query("SELECT c FROM ChatEntity c JOIN c.users u WHERE u.id = ?1")
    Set<ChatEntity> findChatsByUserId(Long userId);
}
