package com.trip.triptogether.dto.response.Recommend;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MedicalFacilityBelovedResponse {
    private Long id;
    private String title;
    private String province;
    private String city;
    private String image;
    private Double rating;
    private Double likes;
}
