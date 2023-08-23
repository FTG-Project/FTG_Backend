package com.trip.triptogether.repository.Board;

import com.trip.triptogether.domain.BoardType;
import com.trip.triptogether.domain.SearchType;
import com.trip.triptogether.dto.response.BoardResponse;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {
    //검색 조건 o
    PageImpl<BoardResponse.PageResponse> getPageListWithSearch(BoardType boardType, SearchType searchType, Pageable pageable);
    //검색 조건 x
    PageImpl<BoardResponse.PageResponse> getBoardList(Pageable pageable);
}
