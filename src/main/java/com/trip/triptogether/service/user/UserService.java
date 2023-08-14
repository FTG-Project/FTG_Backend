package com.trip.triptogether.service.user;

import com.trip.triptogether.domain.User;
import com.trip.triptogether.dto.request.user.UserSaveRequest;
import com.trip.triptogether.dto.response.user.UserSaveResponse;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.security.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    public UserSaveResponse signUp(String userEmail, UserSaveRequest userSaveDto) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new NoSuchElementException("user not found"));

        //signup success -> refreshToken 저장
        String refreshToken = jwtService.createRefreshToken();

        //TODO : S3 연결하면 defaultImageUrl 넣기

        user.signUp(userSaveDto.getNickname(), userSaveDto.getLanguage());
        user.updateRefreshToken(refreshToken);

        return UserSaveResponse.builder().userId(user.getId()).build();
    }

    @Transactional
    public void logout(String userEmail) {
        //TODO : error code 만들면 에러 변경
        User user =  userRepository.findByEmail(userEmail).orElseThrow(
                () -> new NoSuchElementException("user not found"));

        //logout -> remove refreshToken
        user.logout();
    }
}
