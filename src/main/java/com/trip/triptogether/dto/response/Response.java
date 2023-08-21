package com.trip.triptogether.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Response {
    //공통 Response
    @ApiModelProperty(value = "응답 성공여부 : true/false")
    private boolean success;

    @ApiModelProperty(value = "응답 코드 번호")
    private int code;

    public static class GeneralResponse extends Response {
        private String msg;

        public GeneralResponse(boolean success, int code, String msg) {
            super(success, code);
            this.msg = msg;
        }
    }

    @Getter
    public static class SingleResponse<T> extends Response {
        private T data;

        public SingleResponse(boolean success, int code, T data) {
            super(success, code);
            this.data = data;
        }
    }

    @Getter
    public static class ListResponse<T> extends Response {
        private List<T> data;

        public ListResponse(boolean success, int code, List<T> data) {
            super(success, code);
            this.data = data;
        }
    }

}