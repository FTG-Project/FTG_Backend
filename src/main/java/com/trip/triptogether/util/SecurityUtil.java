package com.trip.triptogether.util;

import com.trip.triptogether.domain.User;
import com.trip.triptogether.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    public User getAuthUserOrThrow() {
        String username = getLoginUsername().orElseThrow(
                () -> new NoSuchElementException("user not found"));

        return userRepository.findByEmail(username).orElseThrow(
                () -> new NoSuchElementException("user not found"));
    }

    public Optional<String> getLoginUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        String username = null;

        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails user = (UserDetails) authentication.getPrincipal();
            username = user.getUsername();
        }

        //authentication.getPrincipal()에 값이 제대로 안담기면 null
        return Optional.ofNullable(username);
    }
}
