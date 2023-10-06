package com.trip.triptogether.service.recommend;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import com.trip.triptogether.domain.Recommend;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.Recommend.*;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.repository.recommend.RecommendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        List<RecommendBelovedResponse> recommend = recommendRepository.findTop5RecommendByAreaAndCategoryOrderByRating(area, category);

        return responseService.getListResponse(HttpStatus.OK.value(), recommend,"Top5 장소를 성공적으로 불러왔습니다.");

    }

    public CommonResponse.ListResponse<RecommendBestResponse> recommendBest(String sort) {
        List<RecommendBestResponse> recommend = recommendRepository.findTop10();

        switch (sort) {
            case "scrap":
                Collections.sort(recommend, Comparator.comparingLong(RecommendBestResponse::getScraps).reversed());
                break;
            case "rating":
            default:
                Collections.sort(recommend, Comparator.comparingDouble(RecommendBestResponse::getRating).reversed());
                break;
        }

        return responseService.getListResponse(HttpStatus.OK.value(), recommend,"정렬기준에 따라 성공적으로 정렬하였습니다.");
    }

    public CommonResponse.ListResponse<RecommendRandomResponse> recommendRandom() {
        List<Recommend> recommend = recommendRepository.findRandomRecommend();
        List<RecommendRandomResponse> response = recommend.stream()
                .map(r -> new RecommendRandomResponse(r))
                .collect(toList());

        return responseService.getListResponse(HttpStatus.OK.value(), response,"");

    }

    public CommonResponse.ListResponse<RecommendListResponse> recommendList(Category category, Area area, String sort) {
        List<RecommendListResponse> recommendList = recommendRepository.recommendList(category, area);

        switch (sort) {
            case "rating":
                Collections.sort(recommendList, Comparator.comparingDouble(RecommendListResponse::getRating).reversed());
                break;
            case "scrap":
                Collections.sort(recommendList, Comparator.comparingLong(RecommendListResponse::getScraps).reversed());
                break;
            case "id":
            default:
                break;
        }
        return responseService.getListResponse(HttpStatus.OK.value(), recommendList,"");
    }

    public CommonResponse.SingleResponse<RecommendResponse> recommendDetail(Long id, String sort) {
        Recommend recommend = recommendRepository.findAllFetchJoinReview(id);
        List<ReviewResponse> reviewResponse = recommend.getReviewList().stream()
                .map(review -> new ReviewResponse(review))
                .collect(toList());

        switch (sort) {
            case "highRating":
                Collections.sort(reviewResponse, Comparator.comparingDouble(ReviewResponse::getRating).reversed());
                break;
            case "rowRating":
                Collections.sort(reviewResponse, Comparator.comparingDouble(ReviewResponse::getRating));
                break;
            case "latest":
            default:
                Collections.sort(reviewResponse, Comparator.comparing(ReviewResponse::getUpdatedDate).reversed());
                break;
        }
        return responseService.getSingleResponse(HttpStatus.OK.value(), new RecommendResponse(recommend, reviewResponse),"");
    }
}
