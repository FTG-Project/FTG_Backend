package com.trip.triptogether.security.oauth2.handler;

import com.trip.triptogether.constant.Role;
import com.trip.triptogether.domain.User;
import com.trip.triptogether.repository.UserRepository;
import com.trip.triptogether.security.jwt.service.JwtService;
import com.trip.triptogether.security.oauth2.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 login success");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            // 최초 로그인 요청 -> 회원가입
            if (oAuth2User.getRole() == Role.GUEST) {
                log.info("ROLE : GUEST");
                String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
                response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);

                //response.sendRedirect("tempUrl"); 우리 서비스 국적 선택, 닉네임 선택 창으로 이동

                jwtService.sendAccessAndRefreshToken(response, accessToken, null);

                //TODO : GUEST -> USER, 회원 가입 추가 폼 입력 시 업데이트 되도록 구현하기
                //response.sendRedirect("/sign-in-form"); - >추가 정보 입력으로 redirect, url 아직 안정해져서 나둠.

            } else {
                log.info("ROLE : USER");
                loginSuccess(response, oAuth2User);
                response.sendRedirect("/");
            }
        }catch (Exception e) {
            log.error("occur error in process");
            throw e;
        }
    }

    //TODO : Refresh Token (존재 여부, 만기 여부) -> 발급 여부 결정해야 함.
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
    }
}
