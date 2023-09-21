package com.trip.triptogether.controller.scrap;

import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.user.UserResponse;
import com.trip.triptogether.service.scrap.ScrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scrap")
@RequiredArgsConstructor
public class ScrapController {
    private final ScrapService scrapService;


    @GetMapping("")
    @Operation(summary = "스크랩 조회 api", description = "userId로 스크랩 가져오는 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "retrieve scrap successfully", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    public ResponseEntity<CommonResponse>  getScrapsByUserId(){
        return ResponseEntity.ok().body(scrapService.getScrapsByUserId());
    }

    @PostMapping("/{boardId}")
    @Operation(summary = "스크랩 추가 api", description = "스크랩을 추가하는 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "create scrap successfully", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    public ResponseEntity<CommonResponse> addScrapToBoard(@PathVariable Long boardId){
        return ResponseEntity.ok().body(scrapService.addScrapToBoard(boardId));
    }

    @DeleteMapping("/{boardId}")
    @Operation(summary = "스크랩 삭제 api", description = "스크랩 삭제 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "remove scrap successfully successfully", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    public ResponseEntity<CommonResponse> removeScrapFromBoard(@PathVariable Long boardId){
        return ResponseEntity.ok().body(scrapService.removeScrapFromBoard(boardId));
    }
}
