package com.trip.triptogether.controller.home;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.Recommend.RecommendBelovedResponse;
import com.trip.triptogether.dto.response.Recommend.RecommendBestResponse;
import com.trip.triptogether.dto.response.Recommend.RecommendRandomResponse;
import com.trip.triptogether.service.recommend.RecommendService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/home")
public class HomeController {
    private final RecommendService recommendService;

    @GetMapping
    public ResponseEntity<CommonResponse.ListResponse<RecommendBestResponse>> recommendBest(@RequestParam String sort) {

        return ResponseEntity.ok().body(recommendService.recommendBest(sort));
    }
    @GetMapping("/random")
    public ResponseEntity<CommonResponse.ListResponse<RecommendRandomResponse>> recommendRandom() {

        return ResponseEntity.ok().body(recommendService.recommendRandom());
    }

    @GetMapping("/{Area}")
    public ResponseEntity<CommonResponse.ListResponse<RecommendBelovedResponse>> homeArea(
            @PathVariable("Area") Area area, @RequestParam Category category) {

        return ResponseEntity.ok().body(recommendService.areaBeloved(area, category));
    }
}
