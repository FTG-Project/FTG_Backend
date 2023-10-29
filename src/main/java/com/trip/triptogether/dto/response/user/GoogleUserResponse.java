package com.trip.triptogether.dto.response.user;

import lombok.Data;

@Data
public class GoogleUserResponse {
    private String id;
    private String email;
    private Boolean verified_email;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String locale;
}
