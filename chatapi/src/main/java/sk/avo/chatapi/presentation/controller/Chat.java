package sk.avo.chatapi.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sk.avo.chatapi.application.ApplicationService;
import sk.avo.chatapi.application.dto.ChatAndLastMessageTimestamp;
import sk.avo.chatapi.domain.model.chat.ChatEntity;
import sk.avo.chatapi.domain.model.chat.ChatNotFoundException;
import sk.avo.chatapi.domain.model.chat.UserIsNotInTheChatException;
import sk.avo.chatapi.domain.model.user.UserEntity;
import sk.avo.chatapi.domain.model.user.UserNotFoundException;
import sk.avo.chatapi.presentation.dto.chat.AddUserToChatRequest;
import sk.avo.chatapi.presentation.dto.chat.ChatDetails;
import sk.avo.chatapi.presentation.dto.chat.CreateChatRequest;

import java.util.Date;
import java.util.Set;

@RestController
@RequestMapping("/api/chat/")
public class Chat {
    private final ApplicationService applicationService;

    public Chat(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/")
    public ResponseEntity<ChatDetails> createChat(
            @RequestBody CreateChatRequest createChatRequest,
            Authentication authentication
    ) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        ChatEntity chat;
        Date lastMessageTime;
        try {
            chat = applicationService.createChat(createChatRequest.getName(), userEntity.getId());
            lastMessageTime = applicationService.getChatLastMessageTimestamp(chat.getId(), userEntity.getId());
        } catch (ChatNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserIsNotInTheChatException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.created(null).body(
                new ChatDetails() {{
                    setChatId(chat.getId());
                    setName(chat.getName());
                    setLastMessageTime(lastMessageTime.getTime());
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
            Date lastMessageTime;
            try {
                lastMessageTime = applicationService.getChatLastMessageTimestamp(chatEntity.getId(), userEntity.getId());
            } catch (ChatNotFoundException e) {
                return ResponseEntity.notFound().build();
            } catch (UserIsNotInTheChatException e) {
                return ResponseEntity.badRequest().build();
            }
            chatDetails.add(new ChatDetails() {{
                setChatId(chatEntity.getId());
                setName(chatEntity.getName());
                setLastMessageTime(lastMessageTime.getTime());
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
        ChatAndLastMessageTimestamp chatAndLastMessageTimestamp;
        try {
            chatAndLastMessageTimestamp = applicationService.getChatAndLastMessageTimestamp(chatId, userEntity.getId());
        } catch (ChatNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserIsNotInTheChatException e) {
            return ResponseEntity.badRequest().build();
        }
        ChatDetails chatDetails = new ChatDetails() {{
            setChatId(chatId);
            setName(chatAndLastMessageTimestamp.getChat().getName());
            setLastMessageTime(chatAndLastMessageTimestamp.getLastMessageTimestamp().getTime());
        }};
        return ResponseEntity.ok(chatDetails);
    }

    @PatchMapping("/{chatId}")
    public ResponseEntity<String> updateChat(
            @PathVariable("chatId") Long chatId,
            Authentication authentication
    ) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        return ResponseEntity.internalServerError().body("Not implemented yet.");
    }

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
        } catch (ChatNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserIsNotInTheChatException | UserNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{chatId}/users")
    public void getChatUsers(
            @PathVariable("chatId") Long chatId,
            Authentication authentication
    ) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        // TODO
    }

    @PostMapping("/{chatId}/messages")
    public void createMessage(
            @PathVariable("chatId") Long chatId,
            Authentication authentication
    ) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        // TODO
    }

    @GetMapping("/{chatId}/messages")
    public void getMessages(
            @PathVariable("chatId") Long chatId,
            @RequestParam("page") Integer page,
            Authentication authentication
    ) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        // TODO
    }

    @DeleteMapping("/{chatId}/messages/{messageId}")
    public void deleteMessage(
            @PathVariable("chatId") Long chatId,
            @PathVariable("messageId") Long messageId,
            Authentication authentication
    ) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        // TODO
    }
}
