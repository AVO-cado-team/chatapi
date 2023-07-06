package sk.avo.chatapi.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.avo.chatapi.domain.model.chat.*;
import sk.avo.chatapi.domain.model.user.UserEntity;
import sk.avo.chatapi.domain.repository.ChatRepo;
import sk.avo.chatapi.domain.repository.MessageRepo;
import sk.avo.chatapi.domain.repository.UserRepo;

/**
 * Chat Service
 * is used for chat management.
 * It can CRUD chats and messages.
 * If chat has no users, it will be deleted automatically.
 */

@Service
public class ChatService {
    private final ChatRepo chatRepo;
    private final MessageRepo messageRepo;
    private final UserRepo userRepo;

    @Autowired
    public ChatService(ChatRepo chatRepo, MessageRepo messageRepo, UserRepo userRepo) {
        this.chatRepo = chatRepo;
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
    }

    public ChatEntity createChat(String name) {
        ChatEntity chat = new ChatEntity();
        chat.setName(name);
        chat = chatRepo.save(chat);
        return chat;
    }

    public void addUserToChat(Long userId, long chatId) throws ChatNotFoundException {
        ChatEntity chat = chatRepo.findById(chatId).orElseThrow(ChatNotFoundException::new);
        UserEntity user = userRepo.findById(userId).orElseThrow();
        chat.getUsers().add(user);
        chatRepo.save(chat);
        try {
            createUserJoinMessage(userId, chatId);
        } catch (UserIsNotInTheChatException e) {
            throw new RuntimeException(e); // Unreachable
        }
    }

    public void removeUserFromChat(Long userId, long chatId) throws ChatNotFoundException, UserIsNotInTheChatException {
        ChatEntity chat = chatRepo.findById(chatId).orElseThrow(ChatNotFoundException::new);
        UserEntity user = chat.getUsers().stream().filter(u -> u.getId().equals(userId)).findFirst().orElseThrow(UserIsNotInTheChatException::new);
        chat.getUsers().remove(user);
        chat = chatRepo.save(chat);
        createUserLeaveMessage(userId, chatId);
        if (chat.getUsers().isEmpty()) chatRepo.delete(chat);
    }

    public MessageEntity createMessage(Long userId, Long chatId, String text, Long replyToMessageId, MessageType type, String content)
            throws ChatNotFoundException, UserIsNotInTheChatException {
        ChatEntity chat = chatRepo.findById(chatId).orElseThrow(ChatNotFoundException::new);
        UserEntity user = chat.getUsers().stream().filter(u -> u.getId().equals(userId)).findFirst().orElseThrow(UserIsNotInTheChatException::new);
        MessageEntity replyToMessage = messageRepo.findMessageByChatIdAndMessageId(chatId, replyToMessageId).orElse(null);
        MessageEntity message = new MessageEntity() {{
            setChatId(chatId);
            setSender(user);
            setText(text);
            setReplyTo(replyToMessage);
            setType(type);
            setContent(content);
        }};
        message = messageRepo.save(message);
        return message;
    }

    public MessageEntity createChatCreateMessage(Long userId, Long chatId)
            throws ChatNotFoundException, UserIsNotInTheChatException {
        return createMessage(userId, chatId, null, null, MessageType.CHAT_CREATE, null);
    }

    public MessageEntity createUserJoinMessage(Long userId, Long chatId)
            throws ChatNotFoundException, UserIsNotInTheChatException {
        return createMessage(userId, chatId, null, null, MessageType.USER_JOIN, null);
    }

    public MessageEntity createUserLeaveMessage(Long userId, Long chatId)
            throws ChatNotFoundException, UserIsNotInTheChatException {
        return createMessage(userId, chatId, null, null, MessageType.USER_LEAVE, null);
    }

    public MessageEntity createTextMessage(Long userId, Long chatId, String text, Long replyToMessageId)
            throws ChatNotFoundException, UserIsNotInTheChatException {
        return createMessage(userId, chatId, text, replyToMessageId, MessageType.TEXT, null);
    }

    public MessageEntity createPhotoMessage(Long userId, Long chatId, String text, Long replyToMessageId, String content)
            throws ChatNotFoundException, UserIsNotInTheChatException {
        return createMessage(userId, chatId, text, replyToMessageId, MessageType.PHOTO, content);
    }

    public ChatEntity getChat(Long id) throws ChatNotFoundException {
        return chatRepo.findById(id).orElseThrow(ChatNotFoundException::new);
    }

    public void deleteChat(Long id, Long deletorId) throws ChatNotFoundException, UserIsNotInTheChatException {
        ChatEntity chat = chatRepo.findById(id).orElseThrow(ChatNotFoundException::new);
        chat.getUsers().stream().filter(u -> u.getId().equals(deletorId)).findFirst().orElseThrow(UserIsNotInTheChatException::new);
        chatRepo.delete(chat);
    }

    public void deleteMessage(Long chatId, Long messageId, Long deletorId)
            throws ChatNotFoundException, UserIsNotInTheChatException, MessageNotFoundException {
        chatRepo.findById(chatId).orElseThrow(ChatNotFoundException::new)
                .getUsers().stream().filter(u -> u.getId().equals(deletorId)).findFirst().orElseThrow(UserIsNotInTheChatException::new);
        messageRepo.findMessageByChatIdAndMessageId(chatId, messageId).orElseThrow(MessageNotFoundException::new);
        messageRepo.deleteByChatIdAndMessageId(chatId, messageId);
    }
}
