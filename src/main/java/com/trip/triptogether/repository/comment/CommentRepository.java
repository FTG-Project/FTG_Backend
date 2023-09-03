package com.trip.triptogether.repository.comment;

import com.trip.triptogether.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long>, CommentRepositoryCustom {
}
