package com.trip.triptogether.controller.like;

import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.service.like.LikeService;
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
    public ResponseEntity<CommonResponse.GeneralResponse> updateLike(@PathVariable Long boardId){
        return ResponseEntity.ok().body(likeService.updateLike(boardId));
    }

    //좋아요 취소
    @DeleteMapping("/{boardId}")
    @Transactional
    public ResponseEntity<CommonResponse.GeneralResponse> deleteLike(@PathVariable Long boardId){
        return ResponseEntity.ok().body(likeService.deleteLike(boardId));
    }
}
