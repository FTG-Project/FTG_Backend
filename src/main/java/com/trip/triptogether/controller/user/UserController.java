package com.trip.triptogether.controller.user;

import com.trip.triptogether.constant.Role;
import com.trip.triptogether.dto.request.user.UserSaveRequest;
import com.trip.triptogether.dto.response.user.UserSaveResponse;
import com.trip.triptogether.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserSaveResponse> signUp(@AuthenticationPrincipal UserDetails user,
                                                   @Valid @RequestBody UserSaveRequest userSaveDto) {
        if (user == null) {
            throw new IllegalStateException("user is not exist at db");
        }

        if (user.getAuthorities().contains(new SimpleGrantedAuthority(Role.USER.getKey()))) {
            throw new IllegalArgumentException("already registered user");
        }

        return ResponseEntity.ok(userService.signUp(user.getUsername(), userSaveDto));
    }
}
