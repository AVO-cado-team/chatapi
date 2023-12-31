package sk.avo.chatapi.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import sk.avo.chatapi.application.dto.TokenPair;
import sk.avo.chatapi.domain.model.chat.*;
import sk.avo.chatapi.domain.model.filestorage.FileNotFoundException;
import sk.avo.chatapi.domain.model.security.InvalidTokenException;
import sk.avo.chatapi.domain.model.security.TokenType;
import sk.avo.chatapi.domain.model.user.*;
import sk.avo.chatapi.domain.service.*;
import sk.avo.chatapi.domain.shared.Tuple;
import sk.avo.chatapi.security.shared.UserRoles;

import java.io.File;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
public class ApplicationService {
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final ApplicationContext applicationContext;
    private final ChatService chatService;
    private final RoomService roomService;
    private final Integer pageSize;
    private final FileStorageService fileStorageService;

    /**
     * Call domain service from application service
     *
     * @param domainServiceInterface
     * @param <T>                    domain service interface
     * @return domain service
     */
    public <T> T callDomainService(Class<T> domainServiceInterface) {
        return applicationContext.getBean(domainServiceInterface);
    }

    public ApplicationService(
            UserService userService,
            JwtTokenService jwtTokenService,
            ApplicationContext applicationContext,
            ChatService chatService,
            RoomService roomService, @Value("${application.page-size}") Integer pageSize, FileStorageService fileStorageService) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
        this.applicationContext = applicationContext;
        this.chatService = chatService;
        this.roomService = roomService;
        this.pageSize = pageSize;
        this.fileStorageService = fileStorageService;
    }

    public TokenPair signup(String username, String password, String email) throws UserAlreadyExistsException {
        UserEntity user = userService.createUser(username, password, email);
        TokenPair tokenPair = new TokenPair();
        UserId userId = new UserId(user.getId());
        tokenPair.setAccessToken(jwtTokenService.generateAccessToken(userId));
        tokenPair.setRefreshToken(jwtTokenService.generateRefreshToken(userId));
        return tokenPair;
    }

    public UserEntity verifyEmail(String email, String code) throws UserNotFoundException, UserEmailVerifyException {
        return userService.verifyEmail(email, code);
    }

    public UserEntity regenerateEmailVerificationCode(String email) throws UserNotFoundException, UserEmailIsAlreadyVerifiedException {
        return userService.regenerateEmailVerificationCode(email);
    }

    public TokenPair login(String username, String password)
            throws UserNotFoundException, UserIsNotVerifiedException {
        final UserEntity user = userService.getUserByUsernameAndPassword(username, password);
        final TokenPair tokenPair = new TokenPair();
        final UserId userId = new UserId(user.getId());
        tokenPair.setAccessToken(jwtTokenService.generateAccessToken(userId));
        tokenPair.setRefreshToken(jwtTokenService.generateRefreshToken(userId));
        return tokenPair;
    }

    public TokenPair refresh(String refreshToken) throws InvalidTokenException {
        final Tuple<UserId, String> tokenPayload = jwtTokenService.validateTokenAndGetUserIdAndTokenType(refreshToken);
        if (!tokenPayload.getSecond().equals(TokenType.REFRESH))
            throw new InvalidTokenException();
        final TokenPair tokenPair = new TokenPair();
        final UserId userId = tokenPayload.getFirst();
        tokenPair.setAccessToken(jwtTokenService.generateAccessToken(userId));
        tokenPair.setRefreshToken(jwtTokenService.generateRefreshToken(userId));
        return tokenPair;
    }

    public Tuple<UserId, String> validateTokenAndGetUserIdAndTokenType(final String token) throws InvalidTokenException {
        return jwtTokenService.validateTokenAndGetUserIdAndTokenType(token);
    }

    private MessageEntity sendChatCreateMessage(Long userId, Long chatId)
            throws ChatNotFoundException, UserIsNotInTheChatException {
        return chatService.createMessage(new UserId(userId), new ChatId(chatId), null, null, MessageType.CHAT_CREATE, null);
    }

    private MessageEntity sendUserJoinMessage(Long userId, Long chatId)
            throws ChatNotFoundException, UserIsNotInTheChatException {
        return chatService.createMessage(new UserId(userId), new ChatId(chatId), null, null, MessageType.USER_JOIN, null);
    }

    private MessageEntity sendUserLeaveMessage(Long userId, Long chatId)
            throws ChatNotFoundException, UserIsNotInTheChatException {
        return chatService.createMessage(new UserId(userId), new ChatId(chatId), null, null, MessageType.USER_LEAVE, null);
    }

    public MessageEntity sendTextMessage(Long userId, Long chatId, String text, Long replyToMessageId)
            throws ChatNotFoundException, UserIsNotInTheChatException {
        return chatService.createMessage(new UserId(userId), new ChatId(chatId), text, new MessageId(replyToMessageId), MessageType.TEXT, null);
    }

  public MessageEntity sendPhotoMessage(Long userId, Long chatId, String text, Long replyToMessageId, File content)
          throws ChatNotFoundException, UserIsNotInTheChatException {
    UUID uuid = fileStorageService.storeFile(content);
    return chatService.createMessage(new UserId(userId), new ChatId(chatId), text, new MessageId(replyToMessageId), MessageType.PHOTO, uuid.toString());
  }

  public File getFile(String uuid) throws FileNotFoundException {
    return fileStorageService.getFile(UUID.fromString(uuid));
  }

    public ChatEntity createChat(String name, Long userId) throws ChatNotFoundException {
        ChatEntity chat = chatService.createChat(name);
        try {
            chatService.addUserToChat(new ChatId(chat.getId()), new UserId(userId));
            chat = chatService.getChatAndUserFromChat(new ChatId(chat.getId()), new UserId(userId)).getFirst();
            sendChatCreateMessage(userId, chat.getId());
            sendUserJoinMessage(userId, chat.getId());
        } catch (UserIsNotInTheChatException e) {
            // Fixme - how create chat can throw ChatNotFoundException?
            throw new RuntimeException(e); // Unreachable
        }
        return chat;
    }

    public Set<ChatEntity> getUserChats(Long userId) {
        return chatService.getUserChats(new UserId(userId));
    }

    public Set<UserEntity> getChatUsers(Long chatId, Long userId) throws ChatNotFoundException, UserIsNotInTheChatException {
        return chatService.getChatUsers(new ChatId(chatId), new UserId(userId));
    }

    public ChatEntity getChat(Long chatId, Long userId) throws ChatNotFoundException, UserIsNotInTheChatException {
        return chatService.getChatAndUserFromChat(new ChatId(chatId), new UserId(userId)).getFirst();
    }

    public void deleteChat(Long chatId, Long userId) throws ChatNotFoundException, UserIsNotInTheChatException {
        chatService.deleteChat(new ChatId(chatId), new UserId(userId));
        ChatEntity chat = chatService.getChatAndUserFromChat(new ChatId(chatId), new UserId(userId)).getFirst();
    }

    public void addUserToChat(Long chatId, Long userId, String newUserUsername)
            throws ChatNotFoundException, UserIsNotInTheChatException, UserNotFoundException, UserIsAlreadyInTheChatException {
        Long newUserId = userService.getUserByUsername(newUserUsername).getId();
        chatService.addUserToChat(new ChatId(chatId), new UserId(userId), new UserId(newUserId));
        sendUserJoinMessage(newUserId, chatId);
    }

    public void leaveChat(Long chatId, Long userId) throws ChatNotFoundException, UserIsNotInTheChatException {
        sendUserLeaveMessage(userId, chatId);
        ChatEntity chat = chatService.removeUserFromChat(new UserId(userId), new ChatId(chatId));
        if (chat.getUsers().isEmpty()) {
            chatService.deleteChat(new ChatId(chatId), new UserId(userId));
        }
    }

    public Set<MessageEntity> getMessages(Long chatId, Long userId, Integer page) throws ChatNotFoundException, UserIsNotInTheChatException {
        return chatService.getChatMessages(new ChatId(chatId), new UserId(userId), page, pageSize);
    }

    public void deleteMessage(Long chatId, Long userId, Long messageId) throws MessageNotFoundException, ChatNotFoundException, UserIsNotInTheChatException {
        chatService.deleteMessage(new ChatId(chatId), new MessageId(messageId), new UserId(userId));
    }

    public UserEntity verifyAccessTokenGetUser(String accessToken) throws InvalidTokenException, UserNotFoundException {
        Tuple<UserId, String> tokenPayload = jwtTokenService.validateTokenAndGetUserIdAndTokenType(accessToken);
        if (!tokenPayload.getSecond().equals(TokenType.ACCESS))
            throw new InvalidTokenException();
        return userService.getUserById(tokenPayload.getFirst());
    }


    public Principal authenticate(String token) throws UserNotFoundException, InvalidTokenException {
        UserEntity userEntity = verifyAccessTokenGetUser(token);
        return new UsernamePasswordAuthenticationToken(
                userEntity.getId(),
                null,
                List.of((GrantedAuthority) () -> userEntity.getIsVerified() ? UserRoles.USER_VERIFIED : UserRoles.USER_UNVERIFIED));
    }

    public void handleDisconnect(UserId userId) {
        roomService.removeUserFromAllRooms(userId);
    }

    public void handleSubscribe(UserId userId, ChatId chatId) throws ChatNotFoundException, UserIsNotInTheChatException {
        chatService.getChatAndUserFromChat(chatId, userId);
        roomService.addUserToRoom(chatId, userId);
    }
}
