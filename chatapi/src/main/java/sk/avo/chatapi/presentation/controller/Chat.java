package sk.avo.chatapi.presentation.controller;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sk.avo.chatapi.application.ApplicationService;
import sk.avo.chatapi.domain.model.chat.*;
import sk.avo.chatapi.domain.model.user.UserEntity;
import sk.avo.chatapi.domain.model.user.UserId;
import sk.avo.chatapi.domain.model.user.UserNotFoundException;
import sk.avo.chatapi.presentation.dto.chat.*;

import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/chat/")
public class Chat {
  private final ApplicationService applicationService;

  private final Logger LOG = org.slf4j.LoggerFactory.getLogger(Chat.class);
  private final RoomService roomService;

  private final SimpMessagingTemplate brokerMessagingTemplate;

  public Chat(ApplicationService applicationService, RoomService roomService, SimpMessagingTemplate brokerMessagingTemplate) {
    this.applicationService = applicationService;
    this.roomService = roomService;
    this.brokerMessagingTemplate = brokerMessagingTemplate;
  }

  @PostMapping("/")
  public ResponseEntity<ChatDetails> createChat(
          @RequestBody CreateChatRequest createChatRequest,
          Authentication authentication
  ) {
    UserEntity userEntity = (UserEntity) authentication.getPrincipal();
    ChatEntity chat;
    try {
      chat = applicationService.createChat(createChatRequest.getName(), userEntity.getId());
    } catch (ChatNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.created(null).body(
            new ChatDetails() {{
              setChatId(chat.getId());
              setName(chat.getName());
            }}
    );
  }

  @GetMapping("/")
  public ResponseEntity<Set<ChatDetails>> getChats(
          Authentication authentication
  ) {
    UserEntity userEntity = (UserEntity) authentication.getPrincipal();
    Set<ChatEntity> chatEntities = applicationService.getUserChats(userEntity.getId());
    Set<ChatDetails> chatDetails = new java.util.HashSet<>();
    for (ChatEntity chatEntity : chatEntities) {
      chatDetails.add(new ChatDetails() {{
        setChatId(chatEntity.getId());
        setName(chatEntity.getName());
      }});
    }
    return ResponseEntity.ok(chatDetails);
  }

  @GetMapping("/{chatId}")
  public ResponseEntity<ChatDetails> getChat(
          @PathVariable("chatId") Long chatId,
          Authentication authentication
  ) {
    UserEntity userEntity = (UserEntity) authentication.getPrincipal();
    ChatEntity chat;
    try {
      chat = applicationService.getChat(chatId, userEntity.getId());
    } catch (ChatNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (UserIsNotInTheChatException e) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(
            new ChatDetails() {{
              setChatId(chatId);
              setName(chat.getName());
            }}
    );
  }

//  @PatchMapping("/{chatId}")
//  public ResponseEntity<String> updateChat(
//          @PathVariable("chatId") Long chatId,
//          Authentication authentication
//  ) {
//    UserEntity userEntity = (UserEntity) authentication.getPrincipal();
//    return ResponseEntity.internalServerError().body("Not implemented yet.");
//  }

  @DeleteMapping("/{chatId}")
  public ResponseEntity<Object> deleteChat(
          @PathVariable("chatId") Long chatId,
          Authentication authentication
  ) {
    UserEntity userEntity = (UserEntity) authentication.getPrincipal();
    try {
      applicationService.deleteChat(chatId, userEntity.getId());
    } catch (ChatNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (UserIsNotInTheChatException e) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{chatId}/users")
  public ResponseEntity<Object> addUserToChat(
          @PathVariable("chatId") Long chatId,
          @RequestBody AddUserToChatRequest addUserToChatRequest,
          Authentication authentication
  ) {
    UserEntity userEntity = (UserEntity) authentication.getPrincipal();
    try {
      applicationService.addUserToChat(chatId, userEntity.getId(), addUserToChatRequest.getUsername());
    } catch (ChatNotFoundException | UserNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (UserIsNotInTheChatException e) {
      return ResponseEntity.badRequest().build();
    } catch (UserIsAlreadyInTheChatException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{chatId}/users")
  public ResponseEntity<Set<ChatUser>> getChatUsers(
          @PathVariable("chatId") Long chatId,
          Authentication authentication
  ) {
    UserEntity userEntity = (UserEntity) authentication.getPrincipal();
    Set<ChatUser> chatUsers;
    try {
      chatUsers = applicationService.getChatUsers(chatId, userEntity.getId()).stream().map(
              chatUserEntity -> new ChatUser() {{
                setUserId(chatUserEntity.getId());
                setUsername(chatUserEntity.getUsername());
              }}
      ).collect(java.util.stream.Collectors.toSet());
    } catch (ChatNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (UserIsNotInTheChatException e) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(chatUsers);
  }

  @PostMapping("/{chatId}/messages")
  public ResponseEntity<Object> createMessage(
          @PathVariable("chatId") Long chatId,
          @RequestBody NewMessageRequest newMessageRequest,
          Authentication authentication
  ) {
    UserEntity userEntity = (UserEntity) authentication.getPrincipal();
    MessageEntity messageEntity;
    switch (newMessageRequest.getMessageType()) {
      case "TEXT" -> {
        try {
          messageEntity = applicationService.sendTextMessage(
                  chatId, userEntity.getId(), newMessageRequest.getText(),
                  newMessageRequest.getReplyTo());
        } catch (ChatNotFoundException e) {
          return ResponseEntity.notFound().build();
        } catch (UserIsNotInTheChatException e) {
          return ResponseEntity.badRequest().build();
        }
      }
      default -> {
        return ResponseEntity.badRequest().build();
      }
    }
    return ResponseEntity.ok(messageEntity);
  }

  @GetMapping("/{chatId}/messages")
  public ResponseEntity<Object> getMessages(
          @PathVariable("chatId") Long chatId,
          @RequestParam("page") Integer page,
          Authentication authentication
  ) {
    UserEntity userEntity = (UserEntity) authentication.getPrincipal();
    try {
      return ResponseEntity.ok(applicationService.getMessages(chatId, userEntity.getId(), page));
    } catch (ChatNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (UserIsNotInTheChatException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @DeleteMapping("/{chatId}/messages/{messageId}")
  public ResponseEntity<Object> deleteMessage(
          @PathVariable("chatId") Long chatId,
          @PathVariable("messageId") Long messageId,
          Authentication authentication
  ) {
    UserEntity userEntity = (UserEntity) authentication.getPrincipal();
    try {
      applicationService.deleteMessage(chatId, userEntity.getId(), messageId);
    } catch (ChatNotFoundException | MessageNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (UserIsNotInTheChatException e) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok().build();
  }


  // Handle STOMP messages from client
  @MessageMapping("/chat/{roomId}/send")
  public String sendMessage(
          @DestinationVariable String roomId, Message message,
          SimpMessageHeaderAccessor accessor) {
    LOG.debug("Received message: {}, roomId: {}, user: {}", message, roomId, Objects.requireNonNull(accessor.getUser()).getName());
    UserId userId = new UserId(Long.valueOf(Objects.requireNonNull(accessor.getUser()).getName()));
    if (!roomService.isUserInRoom(new ChatId(Long.valueOf(roomId)), userId)) {
      LOG.debug("User {} is not in room {}", userId, roomId);
      return null;
    }
    try {
      LOG.debug("Sending message to /chat/" + roomId);
      this.applicationService.sendTextMessage(userId.getValue(), Long.valueOf(roomId), message.getPayload().toString(), null);
      this.brokerMessagingTemplate.send("/chat/" + roomId, new GenericMessage<>(message.getPayload(), message.getHeaders()));
    } catch (ChatNotFoundException | UserIsNotInTheChatException e) {
      LOG.debug("Error while sending message to /chat/{} : Error:", roomId, e);
    }

    return null;
  }

}
