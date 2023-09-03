package com.trip.triptogether.repository.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trip.triptogether.domain.Comment;
import com.trip.triptogether.dto.response.CommentResponse;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.*;

import static com.trip.triptogether.domain.QComment.comment;
import static com.trip.triptogether.dto.response.CommentResponse.convertCommentToDto;

public class CommentRepositoryImpl extends QuerydslRepositorySupport implements CommentRepositoryCustom{
    private JPAQueryFactory queryFactory;

    public CommentRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Comment.class);
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public Optional<Comment> findCommentByIdWithParent(Long id) {
        Comment selectedComment=queryFactory.select(comment)
                .from(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(selectedComment);
    }

    @Override
    public List<CommentResponse> findByBoardId(Long id) {

        List<Comment> comments = queryFactory.selectFrom(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.board.id.eq(id))
                .orderBy(comment.parent.id.asc().nullsFirst(),
                        comment.createdDate.asc())
                .fetch();

        List<CommentResponse> commentResponseDTOList = new ArrayList<>();
        Map<Long, CommentResponse> commentDTOHashMap = new HashMap<>();

        comments.forEach(c -> {
            CommentResponse commentResponseDTO = convertCommentToDto(c);
            commentDTOHashMap.put(commentResponseDTO.getId(), commentResponseDTO);
            if (c.getParent() != null) commentDTOHashMap.get(c.getParent().getId()).getChildren().add(commentResponseDTO);
            else commentResponseDTOList.add(commentResponseDTO);
        });
        return commentResponseDTOList;
    }
}


