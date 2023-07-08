package sk.avo.chatapi.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.avo.chatapi.domain.model.chat.MessageEntity;
import sk.avo.chatapi.domain.model.chat.MessageId;

import java.util.Optional;

public interface MessageRepo extends JpaRepository<MessageEntity, MessageId> { // MessageId
    Optional<MessageEntity> findMessageByChatIdAndMessageId(Long chatId, Long messageId);
    void deleteByChatIdAndMessageId(Long chatId, Long messageId);
    Optional<MessageEntity> findFirstByChatIdOrderByTimestampDesc(Long chatId);
}
