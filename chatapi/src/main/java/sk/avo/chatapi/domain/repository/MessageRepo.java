package sk.avo.chatapi.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sk.avo.chatapi.domain.model.chat.MessageEntity;
import sk.avo.chatapi.domain.model.chat.MessageEntityId;

import java.util.Optional;
//import java.util.Set;

public interface MessageRepo extends JpaRepository<MessageEntity, MessageEntityId> {
  Optional<MessageEntity> findMessageByChatIdAndMessageId(Long chatId, Long messageId);
  void deleteByChatIdAndMessageId(Long chatId, Long messageId);
  Optional<MessageEntity> findFirstByChatIdOrderByTimestampDesc(Long chatId);
  Page<MessageEntity> findMessagesByChatId(Long chatId, Pageable pageable);
}
