package sk.avo.chatapi.domain.service;

import sk.avo.chatapi.domain.model.chat.*;
import sk.avo.chatapi.domain.model.user.UserEntity;
import sk.avo.chatapi.domain.model.user.UserId;
import sk.avo.chatapi.domain.shared.Tuple;
import java.util.Date;
import java.util.Set;


public interface ChatService {
  ChatEntity createChat(String name);

  void addUserToChat(ChatId chatId, UserId newUserId) throws ChatNotFoundException;
  void addUserToChat(ChatId chatId, UserId chatMemberId, UserId newUserId)
          throws ChatNotFoundException, UserIsNotInTheChatException, UserIsAlreadyInTheChatException;

  ChatEntity removeUserFromChat(UserId userId, ChatId chatId) throws ChatNotFoundException, UserIsNotInTheChatException;

  MessageEntity createMessage(UserId userId, ChatId chatId, String text, MessageId replyToMessageId, MessageType type, String content)
          throws ChatNotFoundException, UserIsNotInTheChatException;

  ChatEntity getChat(ChatId chatId) throws ChatNotFoundException;

  void deleteChat(ChatId chatId, UserId deletorId) throws ChatNotFoundException, UserIsNotInTheChatException;

  void deleteMessage(ChatId chatId, MessageId messageId, UserId deletorId)
          throws ChatNotFoundException, UserIsNotInTheChatException, MessageNotFoundException;

  Date getLastMessageTimestamp(ChatId chatId, UserId userId) throws ChatNotFoundException, UserIsNotInTheChatException;

  Set<ChatEntity> getUserChats(UserId userId);
  Set<UserEntity> getChatUsers(ChatId chatId, UserId userId) throws ChatNotFoundException, UserIsNotInTheChatException;

  Set<MessageEntity> getChatMessages(ChatId chatId, UserId userId, Integer page, Integer pageSize)
          throws ChatNotFoundException, UserIsNotInTheChatException;

  Tuple<ChatEntity, UserEntity> getChatAndUserFromChat(ChatId chatId, UserId userId)
          throws ChatNotFoundException, UserIsNotInTheChatException;
}
