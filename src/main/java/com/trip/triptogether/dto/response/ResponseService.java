package com.trip.triptogether.dto.response;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ResponseService {
    public Response.GeneralResponse getGeneralResponse(int code, String msg) {
        return new Response.GeneralResponse(true, code, msg);
    }

    public <T> Response.SingleResponse<T> getSingleResponse(int code, T data) {
        return new Response.SingleResponse<>(true, code, data);
    }

    public <T> Response.ListResponse<T> getListResponse(int code, List<T> data) {
        return new Response.ListResponse<>(true, code, data);
    }

}