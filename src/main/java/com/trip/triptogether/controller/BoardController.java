package com.trip.triptogether.controller;

import com.trip.triptogether.domain.BoardType;
import com.trip.triptogether.domain.SearchType;
import com.trip.triptogether.dto.request.BoardRequest;
import com.trip.triptogether.dto.response.BoardResponse;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.service.BoardService;
import com.trip.triptogether.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final S3Service s3Service;
    @GetMapping("")
    public ResponseEntity<PageImpl<BoardResponse.PageResponse>> getBoardList(@RequestParam(required = false) BoardType boardType, @RequestBody SearchType searchCondition, Pageable pageable){
        PageImpl<BoardResponse.PageResponse> responseDTO;
        //검색조건중 모든 내용을 입력하지 않고 요청을 보냈을 때 일반 목록 페이지 출력
        if (searchCondition.getContent().isEmpty() && searchCondition.getWriter().isEmpty() && searchCondition.getTitle().isEmpty()) {
            responseDTO = boardService.getBoardList(pageable);
        } else {
            responseDTO = boardService.getPageListWithSearch(boardType,searchCondition, pageable);

        }
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse.SingleResponse<BoardResponse>> createBoard(UserPrincipal userPrincipal, @RequestPart(value = "boardRequest") BoardRequest boardRequest , @RequestPart(required = false) List<MultipartFile> files ){
        List<String> photoList = new ArrayList<>();

        if (files != null && !files.isEmpty()) { //조건문 빼고?
            photoList = s3Service.upload(files);
        }
        return ResponseEntity.ok().body(boardService.createBoard(userPrincipal,boardRequest,photoList));
    }

}
