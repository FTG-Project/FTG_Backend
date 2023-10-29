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
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("request URI : {}", requestURI);
        if (requestURI.contains("login") || requestURI.contains("favicon") || requestURI.contains("swagger-ui")
            || requestURI.contains("v3") || requestURI.contains("api-docs")) {
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

        // refresh token 이 유효하면 재발급, 아니면 로그아웃
        if (jwtService.isTokenValid(refreshToken)) {
            User user = userRepository.findByRefreshToken(refreshToken).orElseThrow(
                    () -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
            String reIssuedRefreshToken = reIssueRefreshToken(user);
            jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getEmail()),
                    reIssuedRefreshToken);
        } else {
            User user = userRepository.findByRefreshToken(refreshToken).orElseThrow(
                    () -> new CustomException(CustomErrorCode.USER_NOT_FOUND)); // invalid refresh Token

            user.logout(); // refresh Token 기간 만료 -> 로그아웃
        }
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
            String extractEmail = jwtService.extractEmail(accessToken).orElseThrow(
                    () -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));
            User user = userRepository.findByEmail(extractEmail).orElseThrow(
                    () -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
            saveAuthentication(user);
        } else {
            throw new CustomException(CustomErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }

        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(User myUser) {
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
