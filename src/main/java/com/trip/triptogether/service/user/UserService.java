package com.trip.triptogether.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trip.triptogether.common.CustomErrorCode;
import com.trip.triptogether.common.CustomException;
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
    private static final String BEARER = "Bearer ";
    private static final String ACCESS_HEADER = "Authorization";

    @Value("${profile.default}")
    private String defaultProfileImage;
    @Value("${google.infoUrl}")
    private String googleRequestUrl;

    private final JwtService jwtService;
    private final RedisUtil redisUtil;
    private final SecurityUtil securityUtil;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    @Transactional
    public void login(HttpServletResponse response, String googleAccessToken) {
        GoogleUserResponse googleUserResponse = getGoogleUserResponse(googleAccessToken);
        setJwtHeader(response, googleUserResponse);
        userRepository.findByEmail(googleUserResponse.getEmail()).orElseGet(() -> {
            User user = User.builder()
                    .socialId(googleUserResponse.getId())
                    .nickname(googleUserResponse.getName())
                    .role(Role.GUEST)
                    .build();
            return userRepository.save(user);
        });
    }

    private GoogleUserResponse getGoogleUserResponse(String googleAccessToken) {
        HttpEntity<MultiValueMap<String, String>> request = makeRequest(googleAccessToken);
        ResponseEntity<String> response = getResponse(request);
        log.info("body : {}", response.getBody());
        return parseGoogleResponse(response);
    }

    private HttpEntity<MultiValueMap<String, String>> makeRequest(String googleAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS_HEADER, BEARER + googleAccessToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        return request;
    }

    private ResponseEntity<String> getResponse(HttpEntity<MultiValueMap<String, String>> request) {
        ResponseEntity<String> response = restTemplate.exchange(googleRequestUrl,
                HttpMethod.GET, request, String.class);
        return response;
    }

    private GoogleUserResponse parseGoogleResponse(ResponseEntity<String> response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.getBody(), GoogleUserResponse.class);
        } catch (JsonProcessingException e) {
            throw new CustomException(CustomErrorCode.FAIL_JSON_PARSING);
        }
    }

    private void setJwtHeader(HttpServletResponse response, GoogleUserResponse googleUserResponse) {
        String accessToken = jwtService.createAccessToken(googleUserResponse.getEmail());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), accessToken);
        response.addHeader(jwtService.getRefreshHeader(), BEARER + refreshToken);
        log.info("accessToken : {}", accessToken);
    }

    @Transactional
    public void signUp(UserSaveRequest userSaveDto, HttpServletResponse response) {
        User user = securityUtil.getAuthUserOrThrow();
        Role role = securityUtil.getAuthority().orElseThrow(
                () -> new CustomException(CustomErrorCode.INVALID_ACCESS_TOKEN));

        if (role.equals(Role.USER)) {
            throw new CustomException(CustomErrorCode.ALREADY_REGISTERED_USER);
        }

        String refreshToken = jwtService.createRefreshToken();
        jwtService.setRefreshTokenHeader(response, refreshToken);
        user.updateProfileImage(defaultProfileImage);
        user.signUp(userSaveDto.getNickname(), userSaveDto.getLanguage());
        user.updateRefreshToken(refreshToken);
    }

    @Transactional
    public void logout(String accessToken) {
        User user =  securityUtil.getAuthUserOrThrow();
        Long expiration = jwtService.getExpiration(accessToken);
        if (expiration > 0) {
            redisUtil.setBlackList(accessToken, "accessToken", expiration);
        }

        user.logout();
    }
}
