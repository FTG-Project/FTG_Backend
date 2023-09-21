package com.trip.triptogether.controller.user;

import com.trip.triptogether.dto.request.user.UserSaveRequest;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.security.jwt.service.JwtService;
import com.trip.triptogether.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final ResponseService responseService;

    @PostMapping("/sign-up")
    @Operation(summary = "로그인 api", description = "로그인 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success login", content = @Content(schema = @Schema(implementation = CommonResponse.GeneralResponse.class)))})
    public CommonResponse.GeneralResponse signUp(@Valid @RequestBody UserSaveRequest userSaveDto,
                                 HttpServletResponse response) {
        userService.signUp(userSaveDto, response);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "User request completed");
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 api", description = "로그아웃 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success login", content = @Content(schema = @Schema(implementation = CommonResponse.GeneralResponse.class)))})
    public CommonResponse.GeneralResponse logout(HttpServletRequest request) {
        String accessToken = jwtService.extractAccessToken(request).orElseThrow(
                () -> new NoSuchElementException("access Token is not exist"));
        userService.logout(accessToken);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "logout success");
    }
}
