package sk.avo.chatapi;

import org.slf4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final Logger LOG = org.slf4j.LoggerFactory.getLogger(ChatController.class);
    @MessageMapping("/chat")
    @SendTo("/chats/all")
    public String sendMessage(
            @DestinationVariable String roomId, Message message,
                            SimpMessageHeaderAccessor accessor) {
        LOG.info("Received message: {}", message);
        // Get user chats
        // If roomId in chats ->>
        // Send message to all room memmbers

        return message.toString();
    }
}