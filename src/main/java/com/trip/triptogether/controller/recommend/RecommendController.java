package com.trip.triptogether.controller.recommend;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.Recommend.RecommendListResponse;
import com.trip.triptogether.service.recommend.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/recommend")
public class RecommendController {
    private final RecommendService recommendService;

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
}
