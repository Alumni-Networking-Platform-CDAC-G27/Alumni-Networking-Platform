package com.anp.config;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.anp.domain.entities.User;
import com.anp.services.UserService;
import com.anp.utils.responseHandler.exceptions.CustomException;
import com.anp.validations.serviceValidation.services.UserValidationService;
import com.anp.web.websocket.JWTAuthenticationToken;

import static com.anp.utils.constants.ResponseMessageConstants.UNAUTHORIZED_SERVER_ERROR_MESSAGE;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Optional;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class ApplicationWebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final UserService userService;
    private final UserValidationService userValidation;

    private static final String SECRET = "Secret"; // Ideally load from config

    @Autowired
    public ApplicationWebSocketConfiguration(UserService userService, UserValidationService userValidation) {
        this.userService = userService;
        this.userValidation = userValidation;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/socket")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app")
                .setUserDestinationPrefix("/user")
                .enableSimpleBroker("/chat", "/topic", "/queue");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Optional.ofNullable(accessor.getNativeHeader("Authorization")).ifPresent(ah -> {
                        String bearerToken = ah.get(0).replace("Bearer ", "");
                        JWTAuthenticationToken token = getJWTAuthenticationToken(bearerToken);
                        accessor.setUser(token);
                    });
                }
                return message;
            }
        });
    }

    private JWTAuthenticationToken getJWTAuthenticationToken(String token) {
        if (token != null) {
            Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
            JwtParser parser = Jwts.parserBuilder().setSigningKey(key).build();

            String username = parser
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            if (username != null) {
                UserDetails userData = this.userService.loadUserByUsername(username);

                if (!userValidation.isValid(userData)) {
                    throw new CustomException(UNAUTHORIZED_SERVER_ERROR_MESSAGE);
                }

                JWTAuthenticationToken jwtAuthenticationToken =
                        new JWTAuthenticationToken(userData.getAuthorities(), token, (User) userData);
                jwtAuthenticationToken.setAuthenticated(true);

                return jwtAuthenticationToken;
            }
        }
        return null;
    }
}
