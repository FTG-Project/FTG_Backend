package com.trip.triptogether.dto.response.Recommend;

import com.trip.triptogether.domain.Scrap;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScrapResponse {
    private Long userId;
    private Long recommendId;
    private String message;

    public ScrapResponse(Scrap scrap, String message) {
        userId = scrap.getUser().getId();
        recommendId = scrap.getRecommend().getId();
        this.message = message;
    }
}
