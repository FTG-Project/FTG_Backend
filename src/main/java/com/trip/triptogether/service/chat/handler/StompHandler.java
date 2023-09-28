package com.trip.triptogether.service.chat.handler;

import com.trip.triptogether.domain.User;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.security.jwt.service.JwtService;
import com.trip.triptogether.security.jwt.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private static final String BEARER = "Bearer ";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("accessor.getCommand : {}", accessor.getCommand().toString());

        if (StompCommand.CONNECT == accessor.getCommand() || StompCommand.SUBSCRIBE == accessor.getCommand()
             || StompCommand.DISCONNECT == accessor.getCommand()) {
            String accessToken = jwtService.extractTokenFromStompHeader(accessor);

            if (accessToken == null) {
                throw new IllegalStateException("Access Token is null");
            }

            accessToken.replace(BEARER, "");

            if (!jwtService.isTokenValid(accessToken)) {
                throw new IllegalStateException("Access Token is not valid");
            }
        } else if (StompCommand.SEND == accessor.getCommand()) {
            log.info("in send");
            String accessToken = jwtService.extractTokenFromStompHeader(accessor);
            accessToken.replace(BEARER, "");
            log.info("accessToken send : {}", accessToken);

            if (jwtService.isTokenValid(accessToken)) {
                String extractEmail = jwtService.extractEmail(accessToken).orElseThrow(
                        () -> new NoSuchElementException("email is not exist"));
                User user = userRepository.findByEmail(extractEmail).orElseThrow(
                        () -> new NoSuchElementException("user is not exist"));
                saveAuthentication(user);
            }
        }

        return message;
    }

    public void saveAuthentication(User myUser) {
        log.info("saveAuthentication in");
        String password = PasswordUtil.generateRandomPassword();

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(myUser.getEmail())
                .password(password)
                .roles(myUser.getRole().name())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
