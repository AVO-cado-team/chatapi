package sk.avo.chatapi.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.avo.chatapi.domain.model.chat.ChatModel;

public interface ChatRepo extends JpaRepository<ChatModel, Long> {}
