package com.trip.triptogether.service.recommend;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import com.trip.triptogether.domain.Recommend;
import com.trip.triptogether.domain.Review;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.Recommend.*;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.repository.recommend.RecommendRepository;
import com.trip.triptogether.repository.recommend.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class RecommendService {
    private final RecommendRepository recommendRepository;
    private final ResponseService responseService;

    public CommonResponse.ListResponse<RecommendBelovedResponse> areaBeloved(Area area, Category category) {
        Pageable pageable = PageRequest.of(0, 5);

        List<Recommend> recommend = recommendRepository.findTop5RecommendByCombinedScore(category, area, pageable);;
        List<RecommendBelovedResponse> response = recommend.stream()
                .map(r -> new RecommendBelovedResponse(r))
                .collect(toList());

        return responseService.getListResponse(HttpStatus.OK.value(), response);

    }

    public CommonResponse.ListResponse<RecommendBestResponse> recommendBest(String sort) {
        List<RecommendBestResponse> recommend = recommendRepository.findTop10ByOrderByRating(sort);

        return responseService.getListResponse(HttpStatus.OK.value(), recommend);
    }

    public CommonResponse.ListResponse<RecommendRandomResponse> recommendRandom() {
        List<Recommend> recommend = recommendRepository.findRandomRecommend();
        List<RecommendRandomResponse> response = recommend.stream()
                .map(r -> new RecommendRandomResponse(r))
                .collect(toList());

        return responseService.getListResponse(HttpStatus.OK.value(), response);

    }

    public CommonResponse.ListResponse<RecommendListResponse> recommendList(Category category, Area area, String sort) {
        List<RecommendListResponse> recommendList = recommendRepository.recommendList(category, area);

        switch (sort) {
            case "rating":
                Collections.sort(recommendList, Comparator.comparingDouble(RecommendListResponse::getRating).reversed());
                break;
            case "likes":
                Collections.sort(recommendList, Comparator.comparingLong(RecommendListResponse::getLikes).reversed());
                break;
            case "id":
            default:
                break;
        }
        return responseService.getListResponse(HttpStatus.OK.value(), recommendList);
    }

    public CommonResponse.SingleResponse<RecommendResponse> recommendDetail(Long id) {
        Recommend recommend = recommendRepository.findAllFetchJoinReview(id);
        List<ReviewResponse> reviewResponse = recommend.getReviewList().stream()
                .map(review -> new ReviewResponse(review))
                .collect(toList());

        return responseService.getSingleResponse(HttpStatus.OK.value(), new RecommendResponse(recommend, reviewResponse));
    }
}
