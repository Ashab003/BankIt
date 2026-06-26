package com.project.BankIt_backend.config;

import com.project.BankIt_backend.security.JwtService;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;


@Configuration
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    // Assuming you have a JwtService to validate tokens
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public WebSocketSecurityConfig(
            JwtService jwtService,
            UserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // 1. Only intercept the initial CONNECT command
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    // 2. Extract the JWT from the STOMP headers
                    List<String> authorization = accessor.getNativeHeader("Authorization");

                    if (authorization != null && authorization.get(0).startsWith("Bearer ")) {
                        String token = authorization.get(0).substring(7);

                        // 3. Validate token and get username
                        String username = jwtService.extractUsername(token);
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        if (jwtService.isTokenValid(token, userDetails)) {
                            // 4. Create the Principal and assign it to the session!
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );
                            accessor.setUser(authentication);
                        }
                    }
                }
                return message;
            }
        });
    }
}