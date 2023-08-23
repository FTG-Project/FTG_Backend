package com.trip.triptogether.service.recommend;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.domain.Recommend;
import com.trip.triptogether.dto.response.Recommend.*;
import com.trip.triptogether.dto.response.home.HomeAreaResponse;
import com.trip.triptogether.dto.response.home.HomeResponse;
import com.trip.triptogether.repository.RecommendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class RecommendService {
    private final RecommendRepository recommendRepository;

    public HomeAreaResponse areaBeloved(Area area) {
        List<AttractionBelovedResponse> attraction = recommendRepository.findTop5AttractionByCombinedScore(area);
        List<RestaurantBelovedResponse> restaurant = recommendRepository.findTop5RestaurantByCombinedScore(area);
        List<ShowBelovedResponse> show = recommendRepository.findTop5ShowByCombinedScore(area);
        List<MedicalFacilityBelovedResponse> medicalFacility = recommendRepository.findTop5MedicalFacilityByCombinedScore(area);

        return new HomeAreaResponse(area, attraction, restaurant, show, medicalFacility);

    }

    public HomeResponse homeService() {
        List<Recommend> highRatingRecommend = recommendRepository.findTop10ByOrderByRatingDesc();
        List<AllBelovedResponse> highRating = highRatingRecommend.stream()
                .map(r -> new AllBelovedResponse(r))
                .collect(toList());

        List<Recommend> rowRatingRecommend = recommendRepository.findTop10ByOrderByRatingAsc();
        List<AllBelovedResponse> rowRating = rowRatingRecommend.stream()
                .map(r -> new AllBelovedResponse(r))
                .collect(toList());

        List<Recommend> randomRecommend = recommendRepository.findRandomRecommend();
        List<RecommendRandomResponse> random = randomRecommend.stream()
                .map(r -> new RecommendRandomResponse(r))
                .collect(toList());

        return new HomeResponse(highRating, rowRating, random);
    }

}
