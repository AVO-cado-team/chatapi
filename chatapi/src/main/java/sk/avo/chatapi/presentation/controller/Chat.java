package sk.avo.chatapi.presentation.controller;

import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sk.avo.chatapi.application.ApplicationService;
import sk.avo.chatapi.domain.model.chat.ChatEntity;
import sk.avo.chatapi.domain.model.user.UserEntity;
import sk.avo.chatapi.domain.service.ChatService;
import sk.avo.chatapi.presentation.dto.chat.ChatDetails;
import sk.avo.chatapi.presentation.dto.chat.CreateChatRequest;

import java.util.Date;

@RestController
@RequestMapping("/api/chat/")
public class Chat {
    private final ApplicationService applicationService;

    public Chat(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @SneakyThrows
    @PostMapping("/")
    public ResponseEntity<ChatDetails> createChat(
            @RequestBody CreateChatRequest createChatRequest,
            Authentication authentication
    ) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        ChatEntity chat = applicationService
                .callDomainService(ChatService.class)
                .createChat(createChatRequest.getName());
        Date lastMessageTime = applicationService
                .callDomainService(ChatService.class)
                .getLastMessageTimestamp(chat.getId(), userEntity.getId());
        ChatDetails chatDetails = new ChatDetails() {{
            setChatId(chat.getId());
            setName(chat.getName());
            setLastMessageTime(lastMessageTime.getTime());
        }};
        return ResponseEntity.ok(chatDetails);
    }

    @GetMapping("/")
    public void getChats(
            @RequestParam("page") Integer page,
            Authentication authentication
    ) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        // TODO
    }

    @GetMapping("/{chatId}")
    public void getChat(
            @PathVariable("chatId") Long chatId,
            Authentication authentication
    ) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        // TODO
    }

    @PatchMapping("/{chatId}")
    public void updateChat(
            @PathVariable("chatId") Long chatId,
            Authentication authentication
    ) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        // TODO
    }

    @DeleteMapping("/{chatId}")
    public void deleteChat(
            @PathVariable("chatId") Long chatId,
            Authentication authentication
    ) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        // TODO
    }

    @PostMapping("/{chatId}/users")
    public void addUserToChat(
            @PathVariable("chatId") Long chatId,
            Authentication authentication
    ) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        // TODO
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
