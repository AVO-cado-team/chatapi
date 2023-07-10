package sk.avo.chatapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.avo.chatapi.domain.model.chat.RoomService;

@Configuration
public class RoomServiceConfig {
    @Bean
    public RoomService roomService() {
        return new RoomService();
    }
}