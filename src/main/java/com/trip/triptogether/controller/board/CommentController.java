package com.trip.triptogether.controller.board;

//comment

import com.trip.triptogether.dto.request.comment.CommentReqeust;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    //댓글 등록
    @PostMapping("/{boardId}")
    @Operation(summary = "댓글 생성 api", description = "댓글 생성 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "create comment successfully", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    public CommonResponse createComment(@PathVariable Long boardId, @RequestBody CommentReqeust commentReqeust){
        return commentService.createComment(boardId,commentReqeust);
    }

    //댓글 수정 (content만 수정하게 할꺼면 commentRequest 굳이!?)
    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정 api", description = "댓글 수정 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "update comment successfully", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    public CommonResponse updateComment(@PathVariable Long commentId, @RequestBody CommentReqeust commentReqeust){
        return commentService.updateComment(commentId,commentReqeust);
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 t삭제 api", description = "댓글 삭제 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "delete comment successfully", content = @Content(schema = @Schema(implementation = CommonResponse.class)))})
    public CommonResponse deleteComment(@PathVariable Long commentId){
        return commentService.deleteComment(commentId);
    }
}
