package com.trip.triptogether.dto.response.home;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.dto.response.Recommend.RecommendBelovedResponse;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeAreaResponse {
    private Area area;
    private List<RecommendBelovedResponse> attractionId;
    private List<RecommendBelovedResponse> restaurantId;
    private List<RecommendBelovedResponse> showId;

}
