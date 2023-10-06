package com.trip.triptogether.dto.response.Recommend;

import com.trip.triptogether.domain.RecommendScrap;
import com.trip.triptogether.domain.Scrap;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScrapResponse {
    private Long userId;
    private Long recommendId;
    private String message;

    public ScrapResponse(RecommendScrap recommendScrap, String message) {
        userId = recommendScrap.getUser().getId();
        recommendId = recommendScrap.getRecommend().getId();
        this.message = message;
    }
}
