package com.trip.triptogether.repository.Comment;

import com.trip.triptogether.domain.Comment;
import com.trip.triptogether.dto.response.CommentResponse;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryCustom {
    //부모 댓글이 있을 경우 조회
    Optional<Comment> findCommentByIdWithParent(Long id);
    List<CommentResponse> findByBoardId(Long id);
}
