package com.trip.triptogether.controller.like;

import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.service.like.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/heart")
public class LikeController {
    private final LikeService likeService;

    //좋아요 클릭
    @PostMapping("/{boardId}")
    @Transactional
    @Operation(summary = "좋아요 api?(수정해주세요!)", description = "좋아요 api 입니다?")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "like successfully", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    public ResponseEntity<CommonResponse> updateLike(@PathVariable Long boardId){
        return ResponseEntity.ok().body(likeService.updateLike(boardId));
    }

    //좋아요 취소
    @DeleteMapping("/{boardId}")
    @Transactional
    @Operation(summary = "좋아요 취소 api?", description = "좋아요 취소 api 입니다.?")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "cancel like successfully", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    public ResponseEntity<CommonResponse> deleteLike(@PathVariable Long boardId){
        return ResponseEntity.ok().body(likeService.deleteLike(boardId));
    }
}
