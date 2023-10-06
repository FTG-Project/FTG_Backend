package com.trip.triptogether.service.board;

import com.amazonaws.services.kms.model.NotFoundException;
import com.trip.triptogether.common.CustomErrorCode;
import com.trip.triptogether.common.CustomException;
import com.trip.triptogether.domain.*;
import com.trip.triptogether.dto.request.board.BoardRequest;
import com.trip.triptogether.dto.response.board.BoardResponse;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.repository.board.BoardRepository;
import com.trip.triptogether.repository.PhotoRepository;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.service.s3.S3Service;
import com.trip.triptogether.util.RedisUtil;
import com.trip.triptogether.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.trip.triptogether.common.CustomErrorCode.BOARD_NOT_FOUND;
import static com.trip.triptogether.common.CustomErrorCode.NO_USER_PERMISSION;

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

    private final RedisUtil redisUtil;

    //게시글 조회
    @Transactional
    public CommonResponse.SingleResponse<BoardResponse> findBoard(Long boardId){
        User fromUser = securityUtil.getAuthUserOrThrow();
        Board board=boardRepository.findById(boardId)
                .orElseThrow(()->new NotFoundException("could not found board"));
        User user = userRepository.findById(fromUser.getId()).orElseThrow(
                () -> new NotFoundException("user not found"));
        String viewCount = redisUtil.getData(String.valueOf(user.getId()));
        System.out.println(viewCount);
        if (viewCount == null) {
            redisUtil.setDateExpire(String.valueOf(user.getId()), boardId + "_", calculateTimeUntilMidnight());
            board.increaseView();
        } else {
            String[] strArray = viewCount.split("_");
            List<String> redisBoardList = Arrays.asList(strArray);

            boolean isView = false;

            if (!redisBoardList.isEmpty()) {
                for (String redisBoardId : redisBoardList) {
                    if (String.valueOf(boardId).equals(redisBoardId)) {
                        isView = true;
                        break;
                    }
                }
                if (!isView) {
                    viewCount += boardId + "_";

                    redisUtil.setDateExpire(String.valueOf(user.getId()), viewCount, calculateTimeUntilMidnight());
                    board.updateView();
                }
            }
        }
        return responseService.getSingleResponse(HttpStatus.OK.value(), new BoardResponse(board), "게시글을 성공적으로 조회했습니다.");
    }

    public static long calculateTimeUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        // 두 개 날짜 차이 구현 ->ChronoUnit 사용
        LocalDateTime midnight = now.truncatedTo(ChronoUnit.DAYS).plusDays(1);
        return ChronoUnit.SECONDS.between(now, midnight);
    }

    //게시글 등록
    @Transactional
    public CommonResponse.SingleResponse<BoardResponse> createBoard(BoardRequest boardRequest, List<MultipartFile> multipartFileList) {
            User fromUser = securityUtil.getAuthUserOrThrow();
            User user = userRepository.findById(fromUser.getId()).orElseThrow(
                    () -> new NotFoundException("user not found"));

            Board board= Board.builder()
                    .title(boardRequest.getTitle())
                    .contents(boardRequest.getContent())
                    .writer(fromUser.getNickname())
                    .boardType(boardRequest.getType())
                    .profileImage(user.getProfileImage())
                    .user(user)
                    .build();
            user.getBoardList().add(board);

            if (multipartFileList!=null) {
                List<Photo> photoList = new ArrayList<>();
                for (MultipartFile multipartFile : multipartFileList) {
                    Photo photo = Photo.builder()
                            .originFile(multipartFile.getOriginalFilename())
                            .fileSize(multipartFile.getSize())
                            .savedFile(s3Service.upload(multipartFile))
                            .board(board)
                            .build();
                    photoList.add(photo);
                }
                photoRepository.saveAll(photoList);
                board.addPhotoList(photoList);
            }
            Board savedBoard = boardRepository.save(board);
        return responseService.getSingleResponse(HttpStatus.OK.value(), new BoardResponse(savedBoard),"게시글을 성공적으로 등록했습니다.");
    }

    //게시글 조회
    //단순 조회
    @Transactional
    public Slice<BoardResponse.PageResponse> getBoardList(Pageable pageable) {
        Slice<BoardResponse.PageResponse> result = boardRepository.getBoardList(pageable);
        return result;
    }
    //검색 조건 추가해서 조회
    public Slice<BoardResponse.PageResponse> getPageListWithSearch(SortType sortType,BoardType boardType, SearchType searchCondition, Pageable pageable) {
        Slice<BoardResponse.PageResponse> result = boardRepository.getPageListWithSearch(sortType,boardType, searchCondition, pageable);
        return result;
    }

    //게시글 수정
    @Transactional
    public CommonResponse.SingleResponse updateBoard(Long boardId, BoardRequest boardRequest , List<MultipartFile> files) {
            //To do : error 처리
            User user = securityUtil.getAuthUserOrThrow();
            User saveUsers = userRepository.findById(user.getId()).orElseThrow(
                () -> new NoSuchElementException("user not found"));
            Board board = boardRepository.findById(boardId).orElseThrow(() -> new NotFoundException("Board not found!"));

            if (!checkBoardLoginUser(board)) {
                new CustomException(NO_USER_PERMISSION);
            }

            board.updateBoard(boardRequest);

            if (!files.isEmpty()) {
                List<Photo> existingPhotos = photoRepository.findByBoardId(boardId);
                // 기존 photo 삭제
                photoRepository.deleteAll(existingPhotos);
                board.updatePhotoList(existingPhotos);

                for(Photo photo : existingPhotos){
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
            saveUsers.getBoardList().add(board);
            userRepository.save(saveUsers);

            return responseService.getSingleResponse(HttpStatus.OK.value(), new BoardResponse(board),"게시글을 성공적으로 수정했습니다.");
        }

    //게시글 삭제
    @Transactional
    public CommonResponse.SingleResponse deleteBoard(Long boardId) {
        //To do : error 처리
        User user = securityUtil.getAuthUserOrThrow();
        User saveUsers = userRepository.findById(user.getId()).orElseThrow(
                () -> new NoSuchElementException("user not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NotFoundException("Board not found!"));
        List<Photo> photoList=photoRepository.findByBoardId(boardId);

        if (!checkBoardLoginUser(board)) {
            new CustomException(NO_USER_PERMISSION);
        }

        for (Photo existingFile : photoList) {
            s3Service.deleteFile(existingFile.getSavedFile());
        }

        photoRepository.deleteByBoardId(boardId);
        boardRepository.deleteById(boardId);

        return responseService.getSingleResponse(HttpStatus.OK.value(), new BoardResponse(board),"게시글을 성공적으로 삭제했습니다.");
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
