package com.trip.triptogether.dto.response.Recommend;

import com.trip.triptogether.domain.Recommend;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AllBelovedResponse {
    private Long id;
    private String title;
    private String city;
    private String image;
    private Double rating;
    private Double likes;

    public AllBelovedResponse(Recommend recommend) {
        id = recommend.getId();
        title = recommend.getTitle();
        city = recommend.getAddress().getCity();
        image = recommend.getThumbnail();
        rating = recommend.getRating();
        likes = recommend.getLikes();
    }

}
