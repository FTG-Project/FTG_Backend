package com.trip.triptogether.dto.response.Recommend;

import com.trip.triptogether.domain.Recommend;
import com.trip.triptogether.domain.RecommendLikes;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendLikesResponse {
    private Long userId;
    private Long recommendId;

    public RecommendLikesResponse(RecommendLikes recommendLikes) {
        userId = recommendLikes.getUser().getId();
        recommendId = recommendLikes.getRecommend().getId();
    }
}
