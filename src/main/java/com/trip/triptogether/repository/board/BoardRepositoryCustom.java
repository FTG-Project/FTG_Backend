package com.trip.triptogether.repository.board;

import com.trip.triptogether.domain.Board;
import com.trip.triptogether.domain.BoardType;
import com.trip.triptogether.domain.SearchType;
import com.trip.triptogether.domain.SortType;
import com.trip.triptogether.dto.response.board.BoardResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface BoardRepositoryCustom {
    //검색 조건 o
    Slice<BoardResponse.PageResponse> getPageListWithSearch(SortType sortType, BoardType boardType, SearchType searchType, Pageable pageable);
    //검색 조건 x
    Slice<BoardResponse.PageResponse> getBoardList(Pageable pageable);

    void updateLikeCount(Board board);
    void subLikeCount(Board board);
}
