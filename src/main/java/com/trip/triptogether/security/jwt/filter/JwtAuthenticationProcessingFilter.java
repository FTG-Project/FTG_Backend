package com.trip.triptogether.security.jwt.filter;

import com.trip.triptogether.common.CustomErrorCode;
import com.trip.triptogether.common.CustomException;
import com.trip.triptogether.domain.User;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.security.jwt.service.JwtService;
import com.trip.triptogether.security.jwt.util.PasswordUtil;
import com.trip.triptogether.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    private final static List<String> PERMITTED_URL = List.of("login", "favicon", "swagger-ui", "v3", "api-docs");
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("request URI : {}", requestURI);
        if (isPermitUrl(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        //request 로 refreshToken 이 오는 경우는 AccessToken 만료인 경우가 유일, 나머지 null
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        //refreshToken 이 DB에 저장된 값과 일치하는 지 확인하고 AccessToken 재발급
        if (refreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }

        //refreshToken 이 없는 경우
        //AccessToken 검사 -> AccessToken 이 없거나 유효하지 않으면 403
        if (refreshToken == null) {
            log.info("refreshToken == null");
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }

    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new CustomException(CustomErrorCode.INVALID_LOGIN_ACCESS));
        jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getEmail()),
                reIssueRefreshToken(user));
    }

    public String reIssueRefreshToken(User user) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.saveAndFlush(user);
        return reIssuedRefreshToken;
    }

    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication");
        String accessToken = jwtService.extractAccessToken(request).orElseThrow(
                () -> new CustomException(CustomErrorCode.ACCESS_TOKEN_NOT_FOUND));
        if (redisUtil.hasKeyBlackList(accessToken)) {
            throw new CustomException(CustomErrorCode.ALREADY_LOGOUT);
        }

        if (jwtService.isTokenValid(accessToken)) {
            saveAuthentication(getUserByAccessToken(accessToken));
        } else {
            throw new CustomException(CustomErrorCode.INVALID_ACCESS_TOKEN);
        }

        filterChain.doFilter(request, response);
    }

    private User getUserByAccessToken(String accessToken) {
        String extractEmail = jwtService.extractEmail(accessToken).orElseThrow(
                () -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));
        return userRepository.findByEmail(extractEmail).orElseThrow(
                () -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    public void saveAuthentication(User user) {
        String password = PasswordUtil.generateRandomPassword();
        UserDetails userDetails = getUserDetails(user, password);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserDetails getUserDetails(User user, String password) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(password)
                .roles(user.getRole().name())
                .build();
    }

    private boolean isPermitUrl(String requestURI) {
        return PERMITTED_URL.stream().anyMatch(requestURI::contains);
    }
}
