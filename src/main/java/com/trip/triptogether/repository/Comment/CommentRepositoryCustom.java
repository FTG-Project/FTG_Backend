package com.trip.triptogether.repository.Comment;

import com.trip.triptogether.domain.Comment;
import com.trip.triptogether.dto.response.CommentResponse;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryCustom {
    Optional<Comment> findCommentByIdWithParent(Long id);
    List<CommentResponse> findByBoardId(Long id);
}
