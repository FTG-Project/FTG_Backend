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
    public CommonResponse updateLike(Long boardId){
        User user=securityUtil.getAuthUserOrThrow();
        User chkUser=userRepository.findByNickname(user.getNickname())
                .orElseThrow(()->new UsernameNotFoundException("could not found user"));
        Board board=boardRepository.findById(boardId)
                .orElseThrow(()->new NotFoundException("could not found board"));
        if(likeRepository.findByUsersAndBoard(user,board).isPresent()){
            throw new IllegalArgumentException("already exist data");
        }
        Likes likes= Likes.builder()
                .board(board)
                .users(user)
                .build();
        likeRepository.save(likes);
        boardRepository.updateLikeCount(board);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "update like count");

    }
    //좋아요 취소

    public CommonResponse deleteLike(Long boardId) {

        User users=securityUtil.getAuthUserOrThrow();
        User chkUser=userRepository.findByNickname(users.getNickname())
                .orElseThrow(()->new UsernameNotFoundException("could not found user"));

        Board board=boardRepository.findById(boardId)
                .orElseThrow(()->new NotFoundException("Could not found board"));

        Likes likes = likeRepository.findByUsersAndBoard(users, board)
                .orElseThrow(() -> new NotFoundException("Could not found heart id"));

        likeRepository.delete(likes);
        boardRepository.subLikeCount(board);

        return responseService.getGeneralResponse(HttpStatus.OK.value(), "sub like count");
    }


}
