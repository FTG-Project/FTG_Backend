package com.trip.triptogether.service.like;

import com.amazonaws.services.kms.model.NotFoundException;
import com.trip.triptogether.domain.Board;
import com.trip.triptogether.domain.Likes;
import com.trip.triptogether.domain.User;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.repository.board.BoardRepository;
import com.trip.triptogether.repository.like.LikeRepository;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final SecurityUtil securityUtil;
    private final ResponseService responseService;

    //좋아요 클릭
    public CommonResponse.GeneralResponse updateLike(Long boardId){
        User user=securityUtil.getAuthUserOrThrow();
        User chkUser=userRepository.findByNickname(user.getNickname())
                .orElseThrow(()->new UsernameNotFoundException("해당 유저를 찾을 수 없스빈다."));
        Board board=boardRepository.findById(boardId)
                .orElseThrow(()->new NotFoundException("해당 게시글을 찾을 수 없습니다."));
        if(likeRepository.findByUsersAndBoard(user,board).isPresent()){
            throw new IllegalArgumentException("이미 좋아요를 클릭했습니다.");
        }
        Likes likes= Likes.builder()
                .board(board)
                .users(user)
                .build();
        likeRepository.save(likes);
        boardRepository.updateLikeCount(board);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "좋아요를 클릭했습니다.");

    }
    //좋아요 취소

    public CommonResponse.GeneralResponse deleteLike(Long boardId) {

        User users=securityUtil.getAuthUserOrThrow();
        User chkUser=userRepository.findByNickname(users.getNickname())
                .orElseThrow(()->new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));

        Board board=boardRepository.findById(boardId)
                .orElseThrow(()->new NotFoundException("해당 게시글을 찾을 수 없습니다."));

        Likes likes = likeRepository.findByUsersAndBoard(users, board)
                .orElseThrow(() -> new NotFoundException("좋아요를 클릭하지 않았습니다."));

        likeRepository.delete(likes);
        boardRepository.subLikeCount(board);

        return responseService.getGeneralResponse(HttpStatus.OK.value(), "좋아요를 취소했습니다.");
    }


}
