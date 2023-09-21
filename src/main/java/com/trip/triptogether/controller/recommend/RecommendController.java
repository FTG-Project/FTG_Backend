package com.trip.triptogether.controller.recommend;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import com.trip.triptogether.dto.request.recommend.ReviewRequest;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.Recommend.ScrapResponse;
import com.trip.triptogether.dto.response.Recommend.RecommendListResponse;
import com.trip.triptogether.dto.response.Recommend.RecommendResponse;
import com.trip.triptogether.dto.response.Recommend.ReviewResponse;
import com.trip.triptogether.service.recommend.RecommendScrapService;
import com.trip.triptogether.service.recommend.RecommendService;
import com.trip.triptogether.service.recommend.ReviewService;
import com.trip.triptogether.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/recommend")
public class RecommendController {
    private final RecommendService recommendService;
    private final ReviewService reviewService;
    private final RecommendScrapService recommendScrapService;
    private final S3Service s3Service;

    @GetMapping("/{area}")
    public ResponseEntity<CommonResponse.ListResponse<RecommendListResponse>> recommendList(
            @PathVariable Area area, @RequestParam Category category, @RequestParam String sort) {

        return ResponseEntity.ok().body(recommendService.recommendList(category, area, sort));
    }

    @GetMapping("/place")
    public ResponseEntity<CommonResponse.SingleResponse<RecommendResponse>> recommendDetailed(
            @RequestParam Long id, @RequestParam String sort) {

        return ResponseEntity.ok().body(recommendService.recommendDetail(id, sort));
    }

    @PostMapping(value = "/review",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse.SingleResponse<ReviewResponse>> createReview(
            @RequestPart(value = "reviewRequest") ReviewRequest reviewRequest , @RequestPart(required = false) List<MultipartFile> files ,@RequestParam Long recommendId){
        return ResponseEntity.ok().body(reviewService.createReview(reviewRequest, files, recommendId));
    }

    @GetMapping("/scrap")
    public ResponseEntity<CommonResponse.SingleResponse<ScrapResponse>> addScrap(@RequestParam Long id) {
        return ResponseEntity.ok().body(recommendScrapService.addScrap(id));
    }
}