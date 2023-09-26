package com.trip.triptogether.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException{
    CustomErrorCode errorCode; // 상태 정보 + 메시지 담긴 에러 코드
}

