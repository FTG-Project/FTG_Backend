package com.trip.triptogether.service.board;

import com.amazonaws.services.kms.model.NotFoundException;
import com.trip.triptogether.domain.*;
import com.trip.triptogether.dto.request.board.BoardRequest;
import com.trip.triptogether.dto.response.board.BoardResponse;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.repository.board.BoardRepository;
import com.trip.triptogether.repository.PhotoRepository;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.service.s3.S3Service;
import com.trip.triptogether.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j

public class BoardService {
    private final SecurityUtil securityUtil;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final ResponseService responseService;
    private final S3Service s3Service;

    //게시글 등록
    @Transactional
    public CommonResponse.SingleResponse<BoardResponse> createBoard(BoardRequest boardRequest, List<MultipartFile> multipartFileList) {

            User fromUser = securityUtil.getAuthUserOrThrow();
            System.out.println(fromUser);
            User user = userRepository.findById(fromUser.getId()).orElseThrow(
                    () -> new NotFoundException("user not found"));
            Board board= Board.builder()
                    .title(boardRequest.getTitle())
                    .contents(boardRequest.getContent())
                    .writer(fromUser.getNickname())
                    .boardType(boardRequest.getType())
                    .user(user)
                    .build();
            user.getBoardList().add(board);
            Board savedBoard=boardRepository.save(board);

            List<Photo> photoList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFileList) {
            Photo photo=Photo.builder()
                    .originFile(multipartFile.getOriginalFilename())
                    .fileSize(multipartFile.getSize())
                    .savedFile(s3Service.upload(multipartFile))
                    .board(board)
                    .build();
            photoList.add(photo);
        }
        photoRepository.saveAll(photoList);
        return responseService.getSingleResponse(HttpStatus.OK.value(), new BoardResponse(board));
    }

    //게시글 조회
    //To do  -> Slice 찾아보고 수정
    //단순 조회
    public PageImpl<BoardResponse.PageResponse> getBoardList(Pageable pageable) {
        PageImpl<BoardResponse.PageResponse> result = boardRepository.getBoardList(pageable);
        return result;
    }
    //검색 조건 추가해서 조회
    public PageImpl<BoardResponse.PageResponse> getPageListWithSearch(SortType sortType,BoardType boardType, SearchType searchCondition, Pageable pageable) {
        PageImpl<BoardResponse.PageResponse> result = boardRepository.getPageListWithSearch(sortType,boardType, searchCondition, pageable);
        return result;
    }

    //게시글 수정
    @Transactional
    public CommonResponse updateBoard(Long boardId, BoardRequest boardRequest , List<MultipartFile> files) {
            //To do : error 처리
            User user = securityUtil.getAuthUserOrThrow();
            User saveUsers = userRepository.findById(user.getId()).orElseThrow(
                () -> new NoSuchElementException("user not found"));
            Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("Board not found!"));

            if (!checkBoardLoginUser(board)) {
                return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(), "게시글 수정 권한이 없습니다.");
            }

            board.updateBoard(boardRequest);
            Board savedBoard= boardRepository.save(board);

            if (!files.isEmpty()) {
                List<Photo> existingPhotos = photoRepository.findByBoardId(boardId);
                // 기존 photo 삭제
                photoRepository.deleteAll(existingPhotos);

                for(Photo photo : existingPhotos){
                    System.out.println(photo.getSavedFile());
                    s3Service.deleteFile(photo.getSavedFile());
                }

                //새롭게 등록
                List<Photo> photoList = new ArrayList<>();
                for (MultipartFile multipartFile : files) {
                    Photo photo=Photo.builder()
                            .originFile(multipartFile.getOriginalFilename())
                            .fileSize(multipartFile.getSize())
                            .savedFile(s3Service.upload(multipartFile))
                            .board(board)
                            .build();
                    photoList.add(photo);
                }
                photoRepository.saveAll(photoList);
            }
            saveUsers.getBoardList().add(savedBoard);
            userRepository.save(saveUsers);

            return responseService.getSingleResponse(HttpStatus.OK.value(), new BoardResponse(savedBoard));
        }

    //게시글 삭제
    @Transactional
    public CommonResponse deleteBoard(Long boardId) {
        //To do : error 처리
        User user = securityUtil.getAuthUserOrThrow();
        User saveUsers = userRepository.findById(user.getId()).orElseThrow(
                () -> new NoSuchElementException("user not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("Board not found!"));
        List<Photo> photoList=photoRepository.findByBoardId(boardId);
        System.out.println("list"+photoList);

        if (!checkBoardLoginUser(board)) {
            return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(), "게시글 삭제 권한이 없습니다.");
        }

        for (Photo existingFile : photoList) {
            System.out.println("삭제 전 "+existingFile.getSavedFile());
            s3Service.deleteFile(existingFile.getSavedFile());
        }

        photoRepository.deleteByBoardId(boardId);
        boardRepository.deleteById(boardId);

        return responseService.getSingleResponse(HttpStatus.OK.value(), new BoardResponse(board));
    }
    //수정 및 삭제 권한 체크
    private boolean checkBoardLoginUser(Board board) {
        User user = securityUtil.getAuthUserOrThrow();
        if (!Objects.equals(board.getWriter(), user.getNickname())) {
            return false;
        }
        return true;

    }


}
