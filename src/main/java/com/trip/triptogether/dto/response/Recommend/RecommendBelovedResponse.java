package com.trip.triptogether.dto.response.Recommend;

import com.trip.triptogether.domain.Recommend;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendBelovedResponse {
    private Long id;
    private String title;
    private String province;
    private String city;
    private String image;
    private Double rating;
    private Double likes;

    public RecommendBelovedResponse(Recommend r) {
        id = r.getId();
        title = r.getTitle();
        province = r.getAddress().getProvince();
        city = r.getAddress().getCity();
        image = r.getImage();
        rating = r.getRating();
        likes = r.getLikes();
    }
}
