package com.trip.triptogether.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
@Getter
public enum CustomErrorCode {
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),


    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 댓글을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다"),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일이 존재하지 않습니다"),
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "엑세스 토큰이 존재하지 않습니다"),
    //To do : 게시글 작성자만 수정/삭제가 가능합니다?
    NO_USER_PERMISSION(HttpStatus.FORBIDDEN,"해당 글에 접근할 수 있는 권한이 없습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 엑세스 토큰입니다."),
    ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST, "이미 등록된 유저입니다."),
    ALREADY_LOGOUT(HttpStatus.BAD_REQUEST, "이미 로그아웃한 유저입니다"),
    FAIL_JSON_PARSING(HttpStatus.INTERNAL_SERVER_ERROR, "json 파싱에 실패했습니다.");


    private final HttpStatus httpStatus;
    private final String message;

}
