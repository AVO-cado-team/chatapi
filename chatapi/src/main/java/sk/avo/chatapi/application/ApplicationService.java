package sk.avo.chatapi.application;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import sk.avo.chatapi.application.dto.*;
import sk.avo.chatapi.domain.model.chat.ChatEntity;
import sk.avo.chatapi.domain.model.chat.ChatNotFoundException;
import sk.avo.chatapi.domain.model.chat.UserIsAlreadyInTheChatException;
import sk.avo.chatapi.domain.model.chat.UserIsNotInTheChatException;
import sk.avo.chatapi.domain.model.security.InvalidTokenException;
import sk.avo.chatapi.domain.model.user.*;
import sk.avo.chatapi.domain.service.ChatService;
import sk.avo.chatapi.domain.service.JwtTokenService;
import sk.avo.chatapi.domain.service.UserService;
import sk.avo.chatapi.domain.shared.Tuple;
import sk.avo.chatapi.domain.model.security.TokenType;

import java.util.Date;
import java.util.Set;

@Service
public class ApplicationService {
  private final UserService userService;
  private final JwtTokenService jwtTokenService;
  private final ApplicationContext applicationContext;
  private final ChatService chatService;

  /**
   * Call domain service from application service
   * @param domainServiceInterface
   * @return domain service
   * @param <T> domain service interface
   */
  public <T> T callDomainService(Class<T> domainServiceInterface) {
    return applicationContext.getBean(domainServiceInterface);
  }

  public ApplicationService(
          UserService userService,
          JwtTokenService jwtTokenService,
          ApplicationContext applicationContext,
          ChatService chatService) {
    this.userService = userService;
    this.jwtTokenService = jwtTokenService;
    this.applicationContext = applicationContext;
    this.chatService = chatService;
  }

  public TokenPair signup(String username, String password, String email) throws UserAlreadyExistsException {
    UserEntity userEntity = userService.createUser(username, password, email);
    TokenPair tokenPair = new TokenPair();
    tokenPair.setAccessToken(jwtTokenService.generateAccessToken(userEntity.getId()));
    tokenPair.setRefreshToken(jwtTokenService.generateRefreshToken(userEntity.getId()));
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
    tokenPair.setAccessToken(jwtTokenService.generateAccessToken(user.getId()));
    tokenPair.setRefreshToken(jwtTokenService.generateRefreshToken(user.getId()));
    return tokenPair;
  }

  public TokenPair refresh(String refreshToken) throws InvalidTokenException {
    final Tuple<Long, String> tokenPayload = jwtTokenService.validateTokenAndGetUserIdAndTokenType(refreshToken);
    if (!tokenPayload.getSecond().equals(TokenType.REFRESH)) {
      throw new InvalidTokenException();
    }
    final TokenPair tokenPair = new TokenPair();
    tokenPair.setAccessToken(jwtTokenService.generateAccessToken(tokenPayload.getFirst()));
    tokenPair.setRefreshToken(jwtTokenService.generateRefreshToken(tokenPayload.getFirst()));
    return tokenPair;
  }

  public Tuple<Long, String> validateTokenAndGetUserIdAndTokenType(final String token) throws InvalidTokenException {
    return jwtTokenService.validateTokenAndGetUserIdAndTokenType(token);
  }

  public ChatEntity createChat(String name, Long userId) throws ChatNotFoundException {
    ChatEntity chat = chatService.createChat(name);
    try {
      chat = chatService.addFirstUserToChat(userService.getUserById(userId), chat.getId());
      chatService.createChatCreateMessage(userId, chat.getId());
      chatService.createUserJoinMessage(userId, chat.getId());
    } catch (UserNotFoundException | UserIsNotInTheChatException e) {
      throw new RuntimeException(e); // Unreachable
    }
    return chat;
  }

  public Date getChatLastMessageTimestamp(Long chatId, Long userId) throws ChatNotFoundException, UserIsNotInTheChatException {
    return chatService.getLastMessageTimestamp(chatId, userId);
  }

  public Set<ChatEntity> getUserChats(Long userId) {
    return chatService.getUserChats(userId);
  }

  public ChatAndLastMessageTimestamp getChatAndLastMessageTimestamp(Long chatId, Long userId)
          throws ChatNotFoundException, UserIsNotInTheChatException {
    return new ChatAndLastMessageTimestamp() {{
      setChat(chatService.getChatAndUserFromChat(chatId, userId).getFirst());
      setLastMessageTimestamp(chatService.getLastMessageTimestamp(chatId, userId));
    }};
  }

  public void deleteChat(Long chatId, Long userId) throws ChatNotFoundException, UserIsNotInTheChatException {
    chatService.deleteChat(chatId, userId);
  }

  public void addUserToChat(Long chatId, Long userId, String newUserUsername)
          throws ChatNotFoundException, UserIsNotInTheChatException, UserNotFoundException, UserIsAlreadyInTheChatException {
    Long newUserId = userService.getUserByUsername(newUserUsername).getId();
    chatService.addUserToChat(chatId, userId, newUserId);
    chatService.createUserJoinMessage(newUserId, chatId);
  }
}
