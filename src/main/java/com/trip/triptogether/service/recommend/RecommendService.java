package com.trip.triptogether.service.recommend;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.domain.Recommend;
import com.trip.triptogether.dto.response.HomeAreaResponse;
import com.trip.triptogether.dto.response.Recommend.AttractionBelovedResponse;
import com.trip.triptogether.dto.response.Recommend.MedicalFacilityBelovedResponse;
import com.trip.triptogether.dto.response.Recommend.RestaurantBelovedResponse;
import com.trip.triptogether.dto.response.Recommend.ShowBelovedResponse;
import com.trip.triptogether.repository.RecommendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class RecommendService {
    private final RecommendRepository recommendRepository;

    public HomeAreaResponse beloved(Area area) {
        List<AttractionBelovedResponse> attraction = recommendRepository.findTop5AttractionByCombinedScore(area);
        List<RestaurantBelovedResponse> restaurant = recommendRepository.findTop5RestaurantByCombinedScore(area);
        List<ShowBelovedResponse> show = recommendRepository.findTop5ShowByCombinedScore(area);
        List<MedicalFacilityBelovedResponse> medicalFacility = recommendRepository.findTop5MedicalFacilityByCombinedScore(area);

        return new HomeAreaResponse(area, attraction, restaurant, show, medicalFacility);

    }

}
