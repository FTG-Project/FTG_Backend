package com.trip.triptogether.repository.board;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trip.triptogether.domain.Board;
import com.trip.triptogether.domain.BoardType;
import com.trip.triptogether.domain.SearchType;
import com.trip.triptogether.domain.SortType;
import com.trip.triptogether.dto.response.board.BoardResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

import static com.trip.triptogether.domain.QBoard.board;


public class BoardRepositoryImpl extends QuerydslRepositorySupport implements BoardRepositoryCustom {
    private JPAQueryFactory queryFactory;

    public BoardRepositoryImpl (JPAQueryFactory jpaQueryFactory){
        super(Board.class);
        this.queryFactory=jpaQueryFactory;
    }
    @Override
    public void updateLikeCount(Board boards) {

        queryFactory.update(board)
                .set(board.likecnt, board.likecnt.add(1))
                .where(board.eq(boards))
                .execute();

    }

    @Override
    public void subLikeCount(Board boards) {

        queryFactory.update(board)
                .set(board.likecnt, board.likecnt.subtract(1))
                .where(board.eq(boards))
                .execute();

    }

    @Override
    public Slice<BoardResponse.PageResponse> getBoardList(Pageable pageable) {
        JPQLQuery<Board> query = queryFactory
                .selectFrom(board)
                .orderBy(board.createdDate.desc()) // 기본 정렬은 최신순
                .offset(pageable.getOffset()) // 시작점
                .limit(pageable.getPageSize()); // 페이지 사이즈

        // Execute the query and fetch the results
        List<Board> results = query.fetch();

        // Map the results to DTOs
        List<BoardResponse.PageResponse> dtoList = results.stream()
                .map(BoardResponse.PageResponse::new)
                .collect(Collectors.toList());

        Slice<BoardResponse.PageResponse> slice;

        if (dtoList.size() > pageable.getPageSize()) {
            slice = new SliceImpl<>(dtoList, pageable, true);
        } else {
            slice = new SliceImpl<>(dtoList, pageable, false);
        }

        return slice;
    }
    @Override
    public Slice<BoardResponse.PageResponse> getPageListWithSearch(SortType sortType, BoardType boardType, SearchType searchCondition, Pageable pageable) {
        JPQLQuery<Board> query = queryFactory.selectFrom(board);

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(ContentMessageTitleEq(searchCondition.getContent(), searchCondition.getTitle()))
                .and(boardWriterEq(searchCondition.getWriter()));

        if (boardType == BoardType.자유) {
            whereClause.and(board.boardType.eq(BoardType.자유));
        } else if (boardType == BoardType.가이드) {
            whereClause.and(board.boardType.eq(BoardType.가이드));
        }

        if (sortType == SortType.최신순) {
            query.orderBy(board.createdDate.desc());
        } else if (sortType == SortType.조회순) {
            query.orderBy(board.view_cnt.desc());
        }

        query.where(whereClause);

        List<Board> results = getQuerydsl().applyPagination(pageable, query).fetch();
        boolean hasNext = results.size() > pageable.getPageSize();

        // 엔티티 -> Dto 매핑
        List<BoardResponse.PageResponse> dtoList = results.stream()
                .map(BoardResponse.PageResponse::new)
                .collect(Collectors.toList());

        return new SliceImpl<>(dtoList, pageable, hasNext);
    }

    //제목 + 내용에 필요한 동적 쿼리문
    private BooleanExpression ContentMessageTitleEq(String boardContent, String boardTitle){
        // 글 내용 x, 글 제목 o
        if(!boardContent.isEmpty() && !boardTitle.isEmpty()){
            return board.title.contains(boardTitle).or(board.contents.contains(boardContent));
        }

        //글 내용 o, 글 제목 x
        if(!boardContent.isEmpty() && boardTitle.isEmpty()){
            return board.contents.contains(boardContent);
        }

        //글 제목 o
        if(boardContent.isEmpty() && !boardTitle.isEmpty()){
            return board.title.contains(boardTitle);
        }
        return null;
    }
    //  작성자 검색
    private BooleanExpression boardWriterEq(String boardWriter){
        if(boardWriter.isEmpty()){
            return null;
        }
        return board.writer.contains(boardWriter);
    }
}


