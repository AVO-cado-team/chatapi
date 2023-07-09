package sk.avo.chatapi.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sk.avo.chatapi.domain.model.chat.*;
import sk.avo.chatapi.domain.model.user.UserEntity;
import sk.avo.chatapi.domain.repository.ChatRepo;
import sk.avo.chatapi.domain.repository.MessageRepo;
import sk.avo.chatapi.domain.repository.UserRepo;
import sk.avo.chatapi.domain.shared.Tuple;
import java.util.Date;
import java.util.Set;


@Service
public class ChatService {
  private final ChatRepo chatRepo;
  private final MessageRepo messageRepo;
  private final UserRepo userRepo;
  private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ChatService.class);

  @Autowired
  public ChatService(ChatRepo chatRepo, MessageRepo messageRepo, UserRepo userRepo) {
    this.chatRepo = chatRepo;
    this.messageRepo = messageRepo;
    this.userRepo = userRepo;
  }

  public ChatEntity createChat(String name) {
    ChatEntity chat = new ChatEntity();
    chat.setName(name);
    return chatRepo.save(chat);
  }

  public ChatEntity addFirstUserToChat(UserEntity user, Long chatId) throws ChatNotFoundException {
    ChatEntity chat = chatRepo.findById(chatId).orElseThrow(ChatNotFoundException::new);
    chat.addUser(user);
    return chatRepo.save(chat);
  }

  public void addUserToChat(long chatId, Long userId, Long newUserId)
          throws ChatNotFoundException, UserIsNotInTheChatException, UserIsAlreadyInTheChatException {
    getChatAndUserFromChat(chatId, userId);
    ChatEntity chat = chatRepo.findById(chatId).orElseThrow(ChatNotFoundException::new);
    if (chat.getUsers().stream().anyMatch(user -> user.getId().equals(newUserId)))
      throw new UserIsAlreadyInTheChatException();
    UserEntity user = userRepo.findById(newUserId).orElseThrow();
    chat.getUsers().add(user);
    chatRepo.save(chat);
  }

  public ChatEntity removeUserFromChat(Long userId, long chatId) throws ChatNotFoundException, UserIsNotInTheChatException {
    Tuple<ChatEntity, UserEntity> tuple = getChatAndUserFromChat(chatId, userId);
    ChatEntity chat = tuple.getFirst();
    UserEntity user = tuple.getSecond();
    chat.getUsers().remove(user);
    return chatRepo.save(chat);
  }

  public MessageEntity createMessage(Long userId, Long chatId, String text, Long replyToMessageId, MessageType type, String content)
          throws ChatNotFoundException, UserIsNotInTheChatException {
    Tuple<ChatEntity, UserEntity> tuple = getChatAndUserFromChat(chatId, userId);
    MessageEntity replyToMessage = messageRepo.findMessageByChatIdAndMessageId(chatId, replyToMessageId).orElse(null);
    UserEntity user = tuple.getSecond();
    ChatEntity chat = tuple.getFirst();
    MessageEntity message = new MessageEntity();
    message.setChatId(chat.getId());
    message.setSender(user);
    message.setText(text);
    message.setReplyTo(replyToMessage);
    message.setType(type);
    message.setContent(content);
    return messageRepo.save(message);
  }

  public ChatEntity getChat(Long id) throws ChatNotFoundException {
    return chatRepo.findById(id).orElseThrow(ChatNotFoundException::new);
  }

  public void deleteChat(Long chatId, Long deletorId) throws ChatNotFoundException, UserIsNotInTheChatException {
    Tuple<ChatEntity, UserEntity> tuple = getChatAndUserFromChat(chatId, deletorId);
    chatRepo.delete(tuple.getFirst());
  }

  public void deleteMessage(Long chatId, Long messageId, Long deletorId)
          throws ChatNotFoundException, UserIsNotInTheChatException, MessageNotFoundException {
    getChatAndUserFromChat(chatId, deletorId);
    messageRepo.findMessageByChatIdAndMessageId(chatId, messageId).orElseThrow(MessageNotFoundException::new);
    messageRepo.deleteByChatIdAndMessageId(chatId, messageId);
  }

  public Date getLastMessageTimestamp(Long chatId, Long userId) throws ChatNotFoundException, UserIsNotInTheChatException {
    getChatAndUserFromChat(chatId, userId);
    return messageRepo.findFirstByChatIdOrderByTimestampDesc(chatId).orElseThrow(ChatNotFoundException::new).getTimestamp();
  }

  public Set<ChatEntity> getUserChats(Long userId) {
    return chatRepo.findChatsByUserId(userId);
  }
  public Set<UserEntity> getChatUsers(Long chatId, Long userId) throws ChatNotFoundException, UserIsNotInTheChatException {
    getChatAndUserFromChat(chatId, userId);
    return chatRepo.findById(chatId).orElseThrow(ChatNotFoundException::new).getUsers();
  }

  public Set<MessageEntity> getChatMessages(Long chatId, Long userId, Integer page, Integer pageSize)
          throws ChatNotFoundException, UserIsNotInTheChatException {
    getChatAndUserFromChat(chatId, userId);
    return messageRepo.findMessagesByChatId(chatId, PageRequest.of(page, pageSize));
  }

  public Tuple<ChatEntity, UserEntity> getChatAndUserFromChat(Long chatId, Long userId)
          throws ChatNotFoundException, UserIsNotInTheChatException {
    ChatEntity chat = chatRepo.findById(chatId).orElseThrow(ChatNotFoundException::new);
    UserEntity user = chat.getUsers().stream().filter(u -> u.getId().equals(userId)).findFirst().orElseThrow(UserIsNotInTheChatException::new);
    return new Tuple<>(chat, user);
  }
}
