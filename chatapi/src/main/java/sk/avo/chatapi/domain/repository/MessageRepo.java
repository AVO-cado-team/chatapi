package sk.avo.chatapi.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sk.avo.chatapi.domain.model.chat.MessageEntity;
import sk.avo.chatapi.domain.model.chat.MessageId;

import java.util.Optional;
import java.util.Set;

public interface MessageRepo extends JpaRepository<MessageEntity, MessageId> { // MessageId
  Optional<MessageEntity> findMessageByChatIdAndMessageId(Long chatId, Long messageId);
  void deleteByChatIdAndMessageId(Long chatId, Long messageId);
  Optional<MessageEntity> findFirstByChatIdOrderByTimestampDesc(Long chatId);
  Set<MessageEntity> findMessagesByChatId(Long chatId, Pageable pageable);
}
