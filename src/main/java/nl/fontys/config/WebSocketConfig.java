package nl.fontys.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
    //TODO: fix this shit with cors
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/tweets").setAllowedOrigins("http://localhost:4200", "http://localhost:80", "http://localhost", "http://68.183.145.94:80");
        registry.addEndpoint("/tweets").setAllowedOrigins("http://localhost:4200", "http://localhost:80", "http://localhost", "http://68.183.145.94:80").withSockJS();
    }
}