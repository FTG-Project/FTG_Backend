package com.trip.triptogether.repository.recommend;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import com.trip.triptogether.dto.response.Recommend.RecommendListResponse;

import java.util.List;

public interface RecommendRepositoryCustom {
    List<RecommendListResponse> recommendList(Category category, Area area);
}
