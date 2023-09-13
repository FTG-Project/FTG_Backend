package com.trip.triptogether.dto.response.Recommend;

import com.trip.triptogether.domain.Recommend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendListResponse {
    private Long id;
    private String title;
    private String image;
    private String address;
    private Long scraps;
    private Double rating;

}
