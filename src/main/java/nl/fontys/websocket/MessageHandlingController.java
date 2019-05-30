package nl.fontys.websocket;

import nl.fontys.models.entities.Kwetter;
import nl.fontys.websocket.models.OutputMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageHandlingController {
    private SimpMessagingTemplate template;

    @Autowired
    public MessageHandlingController(SimpMessagingTemplate template){
        this.template = template;
    }

    @MessageMapping("/post/tweet")
    public void send(Kwetter message) {
        template.convertAndSend("/topic/tweets", new OutputMessage(message.getAuthor().getId(), message.getPostedOn()));
    }
}
