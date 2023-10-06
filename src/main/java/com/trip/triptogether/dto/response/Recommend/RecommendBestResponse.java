package com.trip.triptogether.dto.response.Recommend;

import com.trip.triptogether.domain.Recommend;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendBestResponse {
    private Long id;
    private String title;
    private String city;
    private String image;
    private Long scraps;
    private Double rating;
}
