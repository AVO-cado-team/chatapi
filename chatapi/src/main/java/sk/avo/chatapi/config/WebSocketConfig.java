package sk.avo.chatapi.config;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import sk.avo.chatapi.application.ApplicationService;
import sk.avo.chatapi.application.exceptions.SubscriptionPathNotFoundException;
import sk.avo.chatapi.domain.model.chat.ChatId;
import sk.avo.chatapi.domain.model.security.InvalidTokenException;
import sk.avo.chatapi.domain.model.user.UserId;
import sk.avo.chatapi.domain.model.user.UserNotFoundException;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketConfig.class);
    private final ApplicationService applicationService;

    private final String tokenHeaderName;

    public WebSocketConfig(ApplicationService applicationService, @Value("${stomp.token.header.name}") String tokenHeaderName) {
        this.applicationService = applicationService;
        this.tokenHeaderName = tokenHeaderName;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/chat");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setPreservePublishOrder(true);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/room").setAllowedOrigins("*");
        registry.addEndpoint("/room").setAllowedOrigins("*").withSockJS();
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authenticationInterceptor());
    }


    public ChannelInterceptor authenticationInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
                StompHeaderAccessor accessor = Objects.requireNonNull(MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class));
                StompCommand command = Objects.requireNonNull(accessor.getCommand());

                try {
                    switch (command) {
                        case CONNECT -> {
                            List<String> headersToken = Optional.ofNullable(accessor.getNativeHeader(tokenHeaderName)).orElseThrow(InvalidTokenException::new);
                            String token = Optional.ofNullable(headersToken.get(0)).orElseThrow(InvalidTokenException::new);
                            Principal user = applicationService.authenticate(token);
                            accessor.setUser(user);
                            LOG.debug("Successfully connected User {} with Token: {}", user.getName(), token);
                        }
                        case SUBSCRIBE -> {
                            Principal user = Optional.ofNullable(accessor.getUser()).orElseThrow(UserNotFoundException::new);
                            String path = Optional.ofNullable(accessor.getDestination()).orElseThrow(SubscriptionPathNotFoundException::new);
                            UserId userId = new UserId(user.getName());
                            ChatId chatId = new ChatId(path.substring(path.lastIndexOf("/") + 1));
                            applicationService.handleSubscribe(userId, chatId);
                            LOG.debug("User subscribed to: {}", accessor.getDestination());
                        }
                        case DISCONNECT -> {
                            Principal user = Optional.ofNullable(accessor.getUser()).orElseThrow(UserNotFoundException::new);
                            UserId userId = new UserId(user.getName());
                            applicationService.handleDisconnect(userId);
                            LOG.debug("User disconnected: {}", accessor.getUser());
                        }
                    }
                } catch (Exception e) {
                    LOG.debug("Error while {} websocket: ", command, e);
                    return null;
                }

                return message;
            }
        };
    }
}