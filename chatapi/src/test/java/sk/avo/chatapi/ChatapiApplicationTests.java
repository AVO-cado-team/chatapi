package sk.avo.chatapi;

import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import sk.avo.chatapi.application.ApplicationService;
import sk.avo.chatapi.application.dto.TokenPair;
import sk.avo.chatapi.domain.model.chat.ChatEntity;
import sk.avo.chatapi.domain.model.chat.ChatNotFoundException;
import sk.avo.chatapi.domain.model.chat.UserIsAlreadyInTheChatException;
import sk.avo.chatapi.domain.model.chat.UserIsNotInTheChatException;
import sk.avo.chatapi.domain.model.user.UserEntity;
import sk.avo.chatapi.domain.model.user.UserIsNotVerifiedException;
import sk.avo.chatapi.domain.model.user.UserNotFoundException;
import sk.avo.chatapi.domain.repository.UserRepo;

import java.util.Set;


@SpringBootTest
class ChatapiApplicationTests {

    public final Logger LOG = org.slf4j.LoggerFactory.getLogger(ChatapiApplicationTests.class);
    @Autowired
    public UserRepo userRepo;
    @Autowired
    private ApplicationService applicationService;


    private StompSessionHandlerAdapter createStompSessionHandler(UserEntity user) {
        class MyStompSessionHandler extends StompSessionHandlerAdapter {
            private final UserEntity user;

            MyStompSessionHandler(UserEntity user) {
                this.user = user;
            }

            @Override
            public void afterConnected(StompSession session, @NotNull StompHeaders connectedHeaders) {
                LOG.debug("{}: New session established : {}", user.getUsername(), session.getSessionId());
                Set<ChatEntity> chats = applicationService.getUserChats(this.user.getId());
                for (ChatEntity chat : chats) {
                    LOG.debug("{}: Subscribing to chat : {} ", this.user.getUsername(), chat.getId());
                    session.subscribe("/chat/" + chat.getId(), this);
                }
            }

            @Override
            public void handleFrame(@NotNull StompHeaders headers, Object payload) {
                LOG.debug("Received : " + payload);
            }
        }

        return new MyStompSessionHandler(user);
    }

    private WebSocketStompClient createClient() {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new StringMessageConverter());
        return stompClient;
    }

    @Test
    public void test() throws ChatNotFoundException, UserNotFoundException, UserIsNotInTheChatException, UserIsAlreadyInTheChatException, UserIsNotVerifiedException {
        String url = "ws://127.0.0.1:8080/room";
        LOG.error("Test started!!!!!!!!!!!!!!!!!@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        WebSocketStompClient stompClient1 = createClient();
        WebSocketStompClient stompClient2 = createClient();
        WebSocketStompClient stompClient3 = createClient();

        // Create user1, user2, user3
        UserEntity user1 = createUser("user1", "user1");
        UserEntity user2 = createUser("user2", "user2");
        UserEntity user3 = createUser("user3", "user3");

        // Create chat1, chat2, chat3
        ChatEntity chat1 = applicationService.createChat("chat1", user1.getId());
        ChatEntity chat2 = applicationService.createChat("chat2", user2.getId());
        ChatEntity chat3 = applicationService.createChat("chat3", user3.getId());

        // Add user2 to chat3
        applicationService.addUserToChat(user3.getId(), chat3.getId(), user2.getUsername());

        // Connect user1, user2, user3 to stomp server
        stompClient1.connectAsync(url, createStompSessionHandler(user1), authenticateUser(user1));
        stompClient2.connectAsync(url, createStompSessionHandler(user2), authenticateUser(user2));
        stompClient3.connectAsync(url, createStompSessionHandler(user3), authenticateUser(user3));
    }

    private StompHeaders authenticateUser(UserEntity user) throws UserNotFoundException, UserIsNotVerifiedException {
        StompHeaders headers = new StompHeaders();
        TokenPair tokens = applicationService.login(user.getUsername(), user.getUsername());
        headers.add("token", tokens.getAccessToken());
        return headers;
    }

    private UserEntity createUser(String username, String password) {
        final String email = username + "@mail.com";
        UserEntity user = new UserEntity();
        user.setUsername(username);
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEmail(email);
        user = userRepo.save(user);
        user.setIsVerified(true);
        return userRepo.save(user);
    }

}
