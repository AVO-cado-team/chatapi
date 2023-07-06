package sk.avo.chatapi.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.avo.chatapi.domain.model.chat.MessageEntity;
import java.util.Optional;

public interface MessageRepo extends JpaRepository<MessageEntity, Long> {
//        @Query("SELECT m FROM MessageEntity m WHERE m.id = ?1 AND m.chat.id = ?2")
    Optional<MessageEntity> findMessageByChatIdAndMessageId(Long chatId, Long messageId);
    void deleteByChatIdAndMessageId(Long chatId, Long messageId);
}
