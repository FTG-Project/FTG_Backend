package com.trip.triptogether.dto.response.Recommend;

import com.trip.triptogether.domain.Recommend;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendListResponse {
    private Long id;

    private String title;
    private String image;
    private Double rating;
    private Double likes;
    private String address;

    public RecommendListResponse(Recommend recommend) {
        id = recommend.getId();
        title = recommend.getTitle();
        image = recommend.getThumbnail();
        rating = recommend.getRating();
        likes = recommend.getLikes();
        address = recommend.getAddress().getProvince() + " " + recommend.getAddress().getCity();
    }
}
