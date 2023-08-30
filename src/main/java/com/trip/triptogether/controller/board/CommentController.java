package com.trip.triptogether.controller.board;

//comment

import com.trip.triptogether.dto.request.comment.CommentReqeust;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.service.comment.CommentService;
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
    public CommonResponse createComment(@PathVariable Long boardId, @RequestBody CommentReqeust commentReqeust){
        return commentService.createComment(boardId,commentReqeust);
    }

    //댓글 수정 (content만 수정하게 할꺼면 commentRequest 굳이!?)
    @PutMapping("/{commentId}")
    public CommonResponse updateComment(@PathVariable Long commentId, @RequestBody CommentReqeust commentReqeust){
        return commentService.updateComment(commentId,commentReqeust);
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    public CommonResponse deleteComment(@PathVariable Long commentId){
        return commentService.deleteComment(commentId);
    }
}
