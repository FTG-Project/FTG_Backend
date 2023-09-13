package com.trip.triptogether.dto.response.Recommend;

import com.trip.triptogether.domain.Recommend;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendBelovedResponse {
    private Long id;
    private String title;
    private String address;
    private String image;
    private Double rating;
    private Double scraps;
}
