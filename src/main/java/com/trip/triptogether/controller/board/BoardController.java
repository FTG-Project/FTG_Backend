package com.trip.triptogether.controller.board;

import com.trip.triptogether.domain.BoardType;
import com.trip.triptogether.domain.SearchType;
import com.trip.triptogether.domain.SortType;
import com.trip.triptogether.dto.request.board.BoardRequest;
import com.trip.triptogether.dto.response.board.BoardResponse;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    //게시글 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<CommonResponse.SingleResponse> findByBoardId (@PathVariable Long boardId){
        return ResponseEntity.ok().body(boardService.findBoard(boardId));
    }

    //게시판 조회
    @GetMapping("")
    public ResponseEntity<Slice<BoardResponse.PageResponse>> getBoardList(@RequestParam(required = false) SortType sortType,
                                                                          @RequestParam(required = false) BoardType boardType,
                                                                          @RequestBody SearchType searchCondition,
                                                                          Pageable pageable){
        Slice<BoardResponse.PageResponse> responseDTO;
        //검색조건 중 모든 내용을 입력하지 않고 요청을 보냈을 때 일반 목록 페이지 출력
        if (searchCondition.getContent().isEmpty() && searchCondition.getWriter().isEmpty() && searchCondition.getTitle().isEmpty()) {
            responseDTO = boardService.getBoardList(pageable);
            System.out.println(responseDTO);
        } else {
            responseDTO = boardService.getPageListWithSearch(sortType,boardType,searchCondition, pageable);
            System.out.println(responseDTO);
        }
        return ResponseEntity.ok().body(responseDTO);
    }

    //게시글 등록
    @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse.SingleResponse> createBoard(@RequestPart(value = "boardRequest") BoardRequest boardRequest , @RequestPart(required = false) List<MultipartFile> files ){
        return ResponseEntity.ok().body(boardService.createBoard(boardRequest,files));
    }
    //게시글 수정
    @PutMapping(value = "/{boardId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse.SingleResponse> updateBoard(@PathVariable Long boardId,@RequestPart(value = "boardRequest") BoardRequest boardRequest , @RequestPart(required = false) List<MultipartFile> files ){

        return ResponseEntity.ok().body(boardService.updateBoard(boardId,boardRequest,files));
    }

    //게시글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<CommonResponse.SingleResponse> deleteBoard(@PathVariable Long boardId){
        return ResponseEntity.ok().body(boardService.deleteBoard(boardId));
    }
}
