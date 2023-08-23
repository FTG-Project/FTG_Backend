package com.trip.triptogether.dto.response.home;

import com.trip.triptogether.dto.response.Recommend.AllBelovedResponse;
import com.trip.triptogether.dto.response.Recommend.RecommendRandomResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeResponse {
    List<AllBelovedResponse> highRating;
    List<AllBelovedResponse> rowRating;
    List<RecommendRandomResponse> random;
}
