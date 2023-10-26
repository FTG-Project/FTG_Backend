package com.trip.triptogether.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trip.triptogether.constant.Role;
import com.trip.triptogether.domain.User;
import com.trip.triptogether.dto.request.user.UserSaveRequest;
import com.trip.triptogether.dto.response.user.GoogleUserResponse;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.security.jwt.service.JwtService;
import com.trip.triptogether.util.RedisUtil;
import com.trip.triptogether.util.SecurityUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final JwtService jwtService;
    private final RedisUtil redisUtil;
    private final SecurityUtil securityUtil;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    @Value("${profile.default}")
    private String defaultProfileImage;

    @Value("${google.infoUrl}")
    private String googleRequestUrl;


    @Transactional
    public void login(HttpServletResponse response, String googleAccessToken) {
        GoogleUserResponse googleUserResponse = getGoogleUserResponse(
                googleAccessToken);

        String accessToken = jwtService.createAccessToken(googleUserResponse.getEmail());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        log.info("Access Token : {}", accessToken);

        Optional<User> findUser = userRepository.findByEmail(googleUserResponse.getEmail());

        if (findUser.isEmpty()) {
            User user = User.builder()
                    .socialId(googleUserResponse.getId())
                    .nickname(googleUserResponse.getName())
                    .role(Role.GUEST)
                    .build();
            userRepository.save(user);
        }
    }

    private GoogleUserResponse getGoogleUserResponse(String googleAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + googleAccessToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> httpResponse = restTemplate.exchange(googleRequestUrl,
                HttpMethod.GET, request, String.class);
        log.info("body : {}", httpResponse.getBody());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            GoogleUserResponse googleUserResponse = objectMapper.readValue(httpResponse.getBody(), GoogleUserResponse.class);
            return googleUserResponse;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException();
        }
    }

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
        user.updateProfileImage(defaultProfileImage);

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
