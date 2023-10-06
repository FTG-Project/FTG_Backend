package com.trip.triptogether.dto.response;


import com.trip.triptogether.common.CustomErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CommonResponse {

    private boolean success;
    private int status;

    public static class ErrorResponse extends CommonResponse {

        private CustomErrorCode errorCode;


        public ErrorResponse(boolean success, int status, CustomErrorCode errorCode) {
            super(success, status);
            this.errorCode = errorCode;

        }
    }

    public static class GeneralResponse extends CommonResponse {
        private String msg;

        public GeneralResponse(boolean success, int status, String msg) {
            super(success, status);
            this.msg = msg;
        }
    }

    @Getter
    public static class SingleResponse<T> extends CommonResponse {
        private T data;
        private String msg;

        public SingleResponse(boolean success, int status, T data, String msg) {
            super(success, status);
            this.data = data;
            this.msg = msg;
        }
    }

    @Getter
    public static class ListResponse<T> extends CommonResponse {
        private List<T> data;

        private String msg;

        public ListResponse(boolean success, int status, List<T> data, String msg) {
            super(success, status);
            this.data = data;
            this.msg = msg;
        }
    }

}
