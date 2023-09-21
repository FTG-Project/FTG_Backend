package com.trip.triptogether.controller.home;

import com.trip.triptogether.constant.Area;
import com.trip.triptogether.constant.Category;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.Recommend.RecommendBelovedResponse;
import com.trip.triptogether.dto.response.Recommend.RecommendBestResponse;
import com.trip.triptogether.dto.response.Recommend.RecommendRandomResponse;
import com.trip.triptogether.service.recommend.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "베스트 장소 리스트 조회 api", description = "베스트 장소 리스트 조회 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "retrieve best recommend successfully", content = @Content(schema = @Schema(implementation = CommonResponse.ListResponse.class)))})
    public ResponseEntity<CommonResponse.ListResponse<RecommendBestResponse>> recommendBest(@RequestParam String sort) {

        return ResponseEntity.ok().body(recommendService.recommendBest(sort));
    }
    @GetMapping("/random")
    @Operation(summary = "랜덤 추천 장소 리스트 조회 api", description = "랜덤 추천 장소 리스트 조회하는 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "retrieve random recommend successfully", content = @Content(schema = @Schema(implementation = CommonResponse.SingleResponse.class)))})
    public ResponseEntity<CommonResponse.ListResponse<RecommendRandomResponse>> recommendRandom() {

        return ResponseEntity.ok().body(recommendService.recommendRandom());
    }

    @GetMapping("/{Area}")
    @Operation(summary = "홈장소? 조회 api 입니다. (수정해주세요!)", description = "홈 장소 조회 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "retrieve home area successfully", content = @Content(schema = @Schema(implementation = CommonResponse.SingleResponse.class)))})
    public ResponseEntity<CommonResponse.ListResponse<RecommendBelovedResponse>> homeArea(
            @PathVariable("Area") Area area, @RequestParam Category category) {

        return ResponseEntity.ok().body(recommendService.areaBeloved(area, category));
    }
}
