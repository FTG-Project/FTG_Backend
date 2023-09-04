package com.trip.triptogether.service.user;

import com.trip.triptogether.constant.Role;
import com.trip.triptogether.domain.User;
import com.trip.triptogether.dto.request.user.UserSaveRequest;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.security.jwt.service.JwtService;
import com.trip.triptogether.util.RedisUtil;
import com.trip.triptogether.util.SecurityUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final JwtService jwtService;
    private final RedisUtil redisUtil;
    private final SecurityUtil securityUtil;

    @Transactional
    public void signUp(UserSaveRequest userSaveDto, HttpServletResponse response) {
        User user = securityUtil.getAuthUserOrThrow();

        Role role = securityUtil.getAuthority().orElseThrow(
                () -> new IllegalStateException("You do not have any authorities."));

        if (role.equals(Role.USER)) {
            throw new IllegalArgumentException("already registered user");
        }

        //setting refreshToken
        String refreshToken = jwtService.createRefreshToken();
        jwtService.setRefreshTokenHeader(response, refreshToken);

        //TODO : S3 연결하면 defaultImageUrl 넣기

        user.signUp(userSaveDto.getNickname(), userSaveDto.getLanguage());

        //signup success -> refreshToken 저장
        user.updateRefreshToken(refreshToken);
    }

    @Transactional
    public void logout(String accessToken) {
        //TODO : error code 만들면 에러 변경
        User user =  securityUtil.getAuthUserOrThrow();

        Long expiration = jwtService.getExpiration(accessToken);
        if (expiration > 0) {
            //access Token 남은 시간만큼 redis 에 저장 -> 해당 accessToken deny
            redisUtil.setBlackList(accessToken, "accessToken", expiration);
        }

        //logout -> remove refreshToken
        user.logout();
    }
}
