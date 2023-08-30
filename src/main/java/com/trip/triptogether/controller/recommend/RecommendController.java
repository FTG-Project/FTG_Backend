package com.trip.triptogether.controller.recommend;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import com.trip.triptogether.dto.request.BoardRequest;
import com.trip.triptogether.dto.request.recommend.ReviewRequest;
import com.trip.triptogether.dto.response.BoardResponse;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.Recommend.RecommendListResponse;
import com.trip.triptogether.dto.response.Recommend.ReviewResponse;
import com.trip.triptogether.service.S3Service;
import com.trip.triptogether.service.recommend.RecommendService;
import com.trip.triptogether.service.recommend.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/recommend")
public class RecommendController {
    private final RecommendService recommendService;
    private final ReviewService reviewService;
    private final S3Service s3Service;

    @GetMapping("/{area}/{category}")
    public ResponseEntity<CommonResponse.ListResponse<RecommendListResponse>> recommendListOrderById(
            @PathVariable Area area, @PathVariable Category category, @RequestParam String sort) {
        CommonResponse.ListResponse<RecommendListResponse> response;

        switch (sort) {
            case "id":
                response = recommendService.recommendListOrderById(category, area);
                break;
            case "rating":
                response = recommendService.recommendListOrderByRating(category, area);
                break;
            case "likes":
                response = recommendService.recommendListOrderByLikes(category, area);
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/review",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse.SingleResponse<ReviewResponse>> createReview(
            @RequestPart(value = "reviewRequest") ReviewRequest reviewRequest , @RequestPart(required = false) List<MultipartFile> files ,@RequestParam Long recommendId){
        List<String> photoList = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            photoList = s3Service.upload(files);
        }
        return ResponseEntity.ok().body(reviewService.createReview(reviewRequest, photoList, recommendId));
    }
}
