package sk.avo.chatapi.config;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import sk.avo.chatapi.application.ApplicationService;
import sk.avo.chatapi.domain.model.security.InvalidTokenException;
import sk.avo.chatapi.domain.model.user.UserEntity;
import sk.avo.chatapi.domain.model.user.UserNotFoundException;
import sk.avo.chatapi.domain.shared.Tuple;
import sk.avo.chatapi.security.model.UserRoles;

import java.util.List;
import java.util.Objects;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketConfig.class);
    private final ApplicationService applicationService;

    public WebSocketConfig(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/chats");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setPreservePublishOrder(true);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setSendTimeLimit(15 * 1000) // Set the send time limit as needed
                .addDecoratorFactory(disconnectEventInterceptor());
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
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    try {
                        String token = Objects.requireNonNull(accessor.getNativeHeader("xxx").get(0));
                        LOG.warn("Token: {}", token);
                        final UserEntity userEntity;
                        Tuple<Long, String> tokenPayloadTuple = null;
                        try {
                            tokenPayloadTuple = applicationService.validateTokenAndGetUserIdAndTokenType(token);
                            userEntity = applicationService.getUserById(tokenPayloadTuple.getFirst());
                        } catch (final InvalidTokenException | UserNotFoundException e) {
                            LOG.info(e.getMessage());
                            return null;
                        }
                        if (!tokenPayloadTuple.getSecond().equals("access")) {
                            LOG.info("token is not access");
                            return null;
                        }
                        boolean isUserVerified = userEntity.getIsVerified();
                        final UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userEntity,
                                        null,
                                        List.of(
                                                (GrantedAuthority)
                                                        () -> isUserVerified ? UserRoles.USER_VERIFIED : UserRoles.USER_UNVERIFIED));
                        accessor.setUser(authentication);
                    } catch (Exception e) {
                        LOG.error("Error: " + e.getMessage());
                        return null;
                    }

                    return message;
                }

                return message;
            }
        };
    }

    @Bean
    public WebSocketHandlerDecoratorFactory disconnectEventInterceptor() {
        return new WebSocketHandlerDecoratorFactory() {
            @Override
            public @NotNull WebSocketHandler decorate(@NotNull WebSocketHandler handler) {
                return new WebSocketHandlerDecorator(handler) {
                    @Override
                    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus closeStatus) throws Exception {
                        LOG.warn("User disconnected: ", session);
                        super.afterConnectionClosed(session, closeStatus);
                    }
                };
            }
        };
    }

    private void handleUserDisconnection() {
        LOG.warn("User disconnected: ");
    }
}