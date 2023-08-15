package com.trip.triptogether.controller.user;

import com.trip.triptogether.constant.Role;
import com.trip.triptogether.dto.request.user.UserSaveRequest;
import com.trip.triptogether.dto.response.user.UserSaveResponse;
import com.trip.triptogether.security.jwt.service.JwtService;
import com.trip.triptogether.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {

    //url 은 api 명세서 작성하고 변경

    private final UserService userService;
    private final JwtService jwtService;

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

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal UserDetails user, HttpServletRequest request) {
        String accessToken = jwtService.extractAccessToken(request).orElseThrow(
                () -> new NoSuchElementException("access Token is not exist"));

        userService.logout(user.getUsername(), accessToken);
    }
}
