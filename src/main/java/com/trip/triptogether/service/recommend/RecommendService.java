package com.trip.triptogether.service.recommend;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import com.trip.triptogether.domain.Recommend;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.Recommend.*;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.dto.response.home.HomeAreaResponse;
import com.trip.triptogether.dto.response.home.HomeResponse;
import com.trip.triptogether.repository.recommend.RecommendRepository;
import com.trip.triptogether.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class RecommendService {
    private final RecommendRepository recommendRepository;
    private final ResponseService responseService;

    public CommonResponse.SingleResponse<HomeAreaResponse> areaBeloved(Area area) {
        Pageable pageable = PageRequest.of(0, 5);
        List<RecommendBelovedResponse> attraction = recommendRepository.findTop5RecommendByCombinedScore(Category.ATTRACTION, area, pageable);
        List<RecommendBelovedResponse> restaurant = recommendRepository.findTop5RecommendByCombinedScore(Category.RESTAURANT, area, pageable);
        List<RecommendBelovedResponse> show = recommendRepository.findTop5RecommendByCombinedScore(Category.SHOW, area, pageable);

        return responseService.getSingleResponse(HttpStatus.OK.value(), new HomeAreaResponse(area, attraction, restaurant, show));

    }

    public CommonResponse.SingleResponse<HomeResponse> homeService() {
        List<Recommend> highRatingRecommend = recommendRepository.findTop10ByOrderByRatingDesc();
        List<RecommendBestResponse> highRating = highRatingRecommend.stream()
                .map(r -> new RecommendBestResponse(r))
                .collect(toList());

        List<Recommend> rowRatingRecommend = recommendRepository.findTop10ByOrderByRatingAsc();
        List<RecommendBestResponse> rowRating = rowRatingRecommend.stream()
                .map(r -> new RecommendBestResponse(r))
                .collect(toList());

        List<Recommend> randomRecommend = recommendRepository.findRandomRecommend();
        List<RecommendRandomResponse> random = randomRecommend.stream()
                .map(r -> new RecommendRandomResponse(r))
                .collect(toList());

        return responseService.getSingleResponse(HttpStatus.OK.value(), new HomeResponse(highRating, rowRating, random));
    }

    public CommonResponse.ListResponse<RecommendListResponse> recommendListOrderById(Category category, Area area) {
        List<Recommend> recommend = recommendRepository.findByCategoryAndAreaOrderById(category, area);
        List<RecommendListResponse> response = recommend.stream()
                .map(r -> new RecommendListResponse(r))
                .collect(toList());

        return responseService.getListResponse(HttpStatus.OK.value(), response);
    }

    public CommonResponse.ListResponse<RecommendListResponse> recommendListOrderByRating(Category category, Area area) {
        List<Recommend> recommend = recommendRepository.findByCategoryAndAreaOrderByRatingDesc(category, area);
        List<RecommendListResponse> response = recommend.stream()
                .map(r -> new RecommendListResponse(r))
                .collect(toList());

        return responseService.getListResponse(HttpStatus.OK.value(), response);
    }

    public CommonResponse.ListResponse<RecommendListResponse> recommendListOrderByLikes(Category category, Area area) {
        List<Recommend> recommend = recommendRepository.findByCategoryAndAreaOrderByLikesDesc(category, area);
        List<RecommendListResponse> response = recommend.stream()
                .map(r -> new RecommendListResponse(r))
                .collect(toList());

        return responseService.getListResponse(HttpStatus.OK.value(), response);
    }

}
