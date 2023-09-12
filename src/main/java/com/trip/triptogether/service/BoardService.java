package com.trip.triptogether.service;

import com.trip.triptogether.domain.*;
import com.trip.triptogether.dto.request.BoardRequest;
import com.trip.triptogether.dto.response.BoardResponse;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.repository.Board.BoardRepository;
import com.trip.triptogether.repository.Comment.CommentRepository;
import com.trip.triptogether.repository.PhotoRepository;
import com.trip.triptogether.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final ResponseService responseService;

    @Transactional
    public CommonResponse.SingleResponse<BoardResponse> createBoard(UserPrincipal userPrincipal, BoardRequest boardRequest, List<String> files) {
            User user = userRepository.findByNickname(userPrincipal.getName()).orElseThrow(() -> new UsernameNotFoundException(userPrincipal.getName()));
            System.out.println(user.getEmail());
            Board board= Board.builder()
                    .title(boardRequest.getTitle())
                    .contents(boardRequest.getContent())
                    .writer(userPrincipal.getName())
                    .boardType(boardRequest.getBoardType())
                    .user(user)
                    .build();
            user.getBoardList().add(board);

            List<String> photoList = new ArrayList<>();

            for (String file : files) {
                Photo photo =new Photo(file,board);
                photoRepository.save(photo);
                board.addPhoto(photo);
                photoList.add(photo.getSavedFile());

            }
            return responseService.getSingleResponse(HttpStatus.OK.value(), new BoardResponse(board));
    }

    //단순 조회
    @Transactional
    public PageImpl<BoardResponse.PageResponse> getBoardList(Pageable pageable) {
        PageImpl<BoardResponse.PageResponse> result = boardRepository.getBoardList(pageable);
        return result;
    }
        @Transactional
        public PageImpl<BoardResponse.PageResponse> getPageListWithSearch(BoardType boardType, SearchType searchCondition, Pageable pageable) {
            PageImpl<BoardResponse.PageResponse> result = boardRepository.getPageListWithSearch(boardType, searchCondition, pageable);
            return result;
        }

}
