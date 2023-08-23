package com.trip.triptogether.repository.Board;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trip.triptogether.domain.Board;
import com.trip.triptogether.domain.BoardType;
import com.trip.triptogether.domain.SearchType;
import com.trip.triptogether.dto.response.BoardResponse;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public PageImpl<BoardResponse.PageResponse> getBoardList(Pageable pageable) {
        List<Board> results = queryFactory
                .selectFrom(board)
                .orderBy(board.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory
                .selectFrom(board)
                .fetchCount();

        List<BoardResponse.PageResponse> dtoList = results.stream()
                .map(BoardResponse.PageResponse::new)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, totalCount);
    }
    @Override
    public PageImpl<BoardResponse.PageResponse> getPageListWithSearch(BoardType boardType, SearchType searchCondition, Pageable pageable){
        JPQLQuery<Board> query = queryFactory.select(board).from(board);
        //동적 쿼리를 위해 BooleanBuilder(querydsl에서 제공하는 클래스) 사용
        BooleanBuilder whereClause = new BooleanBuilder();
        //whereClause 기준에 맞는 레코드만 출력
        whereClause.and(ContentMessageTitleEq(searchCondition.getContent(), searchCondition.getTitle()))
                .and(boardWriterEq(searchCondition.getWriter()));

        //전체 레코드 포함?
        if (boardType == BoardType.자유) {
            whereClause.and(board.boardType.eq(BoardType.자유));
        } else if (boardType == BoardType.가이드) {
            whereClause.and(board.boardType.eq(BoardType.가이드));
        }

        query.where(whereClause).orderBy(board.createdDate.desc());

        List<Board> results = getQuerydsl().applyPagination(pageable, query).fetch();
        long totalCount = query.fetchCount();

        // entity->response dto
        List<BoardResponse.PageResponse> dtoList = results.stream()
                .map(BoardResponse.PageResponse::new)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, totalCount);
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


