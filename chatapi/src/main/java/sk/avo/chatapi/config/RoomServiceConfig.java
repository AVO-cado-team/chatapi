package sk.avo.chatapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.avo.chatapi.application.impl.RoomServiceImpl;

@Configuration
public class RoomServiceConfig {
    @Bean
    public RoomServiceImpl roomService() {
        return new RoomServiceImpl();
    }
}