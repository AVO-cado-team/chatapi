package sk.avo.chatapi.application.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sk.avo.chatapi.domain.model.chat.*;
import sk.avo.chatapi.domain.model.user.UserEntity;
import sk.avo.chatapi.domain.model.user.UserId;
import sk.avo.chatapi.domain.repository.ChatRepo;
import sk.avo.chatapi.domain.repository.MessageRepo;
import sk.avo.chatapi.domain.repository.UserRepo;
import sk.avo.chatapi.domain.service.ChatService;
import sk.avo.chatapi.domain.shared.Tuple;

import java.util.Date;
import java.util.Set;


@Service
public class ChatServiceImpl implements ChatService {
  private final ChatRepo chatRepo;
  private final MessageRepo messageRepo;
  private final UserRepo userRepo;
  private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ChatServiceImpl.class);

  @Autowired
  public ChatServiceImpl(ChatRepo chatRepo, MessageRepo messageRepo, UserRepo userRepo) {
    this.chatRepo = chatRepo;
    this.messageRepo = messageRepo;
    this.userRepo = userRepo;
  }

  @Override
  public ChatEntity createChat(String name) {
    ChatEntity chat = new ChatEntity();
    chat.setName(name);
    return chatRepo.save(chat);
  }

  @Override
  public void addUserToChat(ChatId chatId, UserId newUserId) throws ChatNotFoundException {
    ChatEntity chat = chatRepo.findById(chatId.getValue()).orElseThrow(ChatNotFoundException::new);
    chat.addUser(userRepo.findById(newUserId.getValue()).orElseThrow());
    chatRepo.save(chat);
  }

  @Override
  public void addUserToChat(ChatId chatId, UserId chatMemberId, UserId newUserId)
          throws ChatNotFoundException, UserIsNotInTheChatException, UserIsAlreadyInTheChatException {
    getChatAndUserFromChat(chatId, chatMemberId);
    ChatEntity chat = chatRepo.findById(chatId.getValue()).orElseThrow(ChatNotFoundException::new);
    if (chat.getUsers().stream().anyMatch(user -> user.getId().equals(newUserId.getValue())))
      throw new UserIsAlreadyInTheChatException();
    UserEntity user = userRepo.findById(newUserId.getValue()).orElseThrow();
    chat.getUsers().add(user);
    chatRepo.save(chat);
  }

  public ChatEntity removeUserFromChat(UserId userId, ChatId chatId) throws ChatNotFoundException, UserIsNotInTheChatException {
    Tuple<ChatEntity, UserEntity> tuple = getChatAndUserFromChat(chatId, userId);
    ChatEntity chat = tuple.getFirst();
    UserEntity user = tuple.getSecond();
    chat.getUsers().remove(user);
    return chatRepo.save(chat);
  }

  public MessageEntity createMessage(UserId userId, ChatId chatId, String text, MessageId replyToMessageId, MessageType type, String content)
          throws ChatNotFoundException, UserIsNotInTheChatException {
    Tuple<ChatEntity, UserEntity> tuple = getChatAndUserFromChat(chatId, userId);
    MessageEntity replyToMessage = replyToMessageId == null ? null : messageRepo.findMessageByChatIdAndMessageId(chatId.getValue(), replyToMessageId.getValue()).orElse(null);
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

  public ChatEntity getChat(ChatId chatId) throws ChatNotFoundException {
    return chatRepo.findById(chatId.getValue()).orElseThrow(ChatNotFoundException::new);
  }

  public void deleteChat(ChatId chatId, UserId deletorId) throws ChatNotFoundException, UserIsNotInTheChatException {
    Tuple<ChatEntity, UserEntity> tuple = getChatAndUserFromChat(chatId, deletorId);
    chatRepo.delete(tuple.getFirst());
  }

  public void deleteMessage(ChatId chatId, MessageId messageId, UserId deletorId)
          throws ChatNotFoundException, UserIsNotInTheChatException, MessageNotFoundException {
    getChatAndUserFromChat(chatId, deletorId);
    messageRepo.findMessageByChatIdAndMessageId(chatId.getValue(), messageId.getValue()).orElseThrow(MessageNotFoundException::new);
    messageRepo.deleteByChatIdAndMessageId(chatId.getValue(), messageId.getValue());
  }

  public Date getLastMessageTimestamp(ChatId chatId, UserId userId) throws ChatNotFoundException, UserIsNotInTheChatException {
    getChatAndUserFromChat(chatId, userId);
    return messageRepo.findFirstByChatIdOrderByTimestampDesc(chatId.getValue()).orElseThrow(ChatNotFoundException::new).getTimestamp();
  }

  public Set<ChatEntity> getUserChats(UserId userId) {
    return chatRepo.findChatsByUserId(userId.getValue());
  }
  public Set<UserEntity> getChatUsers(ChatId chatId, UserId userId) throws ChatNotFoundException, UserIsNotInTheChatException {
    getChatAndUserFromChat(chatId, userId);
    return chatRepo.findById(chatId.getValue()).orElseThrow(ChatNotFoundException::new).getUsers();
  }

  public Set<MessageEntity> getChatMessages(ChatId chatId, UserId userId, Integer page, Integer pageSize)
          throws ChatNotFoundException, UserIsNotInTheChatException {
    getChatAndUserFromChat(chatId, userId);
    return messageRepo.findMessagesByChatId(chatId.getValue(), PageRequest.of(page, pageSize))
            .stream().collect(java.util.stream.Collectors.toSet());
  }

  public Tuple<ChatEntity, UserEntity> getChatAndUserFromChat(ChatId chatId, UserId userId)
          throws ChatNotFoundException, UserIsNotInTheChatException {
    ChatEntity chat = chatRepo.findById(chatId.getValue()).orElseThrow(ChatNotFoundException::new);
    UserEntity user = chat.getUsers().stream().filter(u -> u.getId().equals(userId.getValue())).findFirst().orElseThrow(UserIsNotInTheChatException::new);
    return new Tuple<>(chat, user);
  }
}
