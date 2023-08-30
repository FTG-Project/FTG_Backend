package com.trip.triptogether.service.comment;

import com.amazonaws.services.kms.model.NotFoundException;
import com.trip.triptogether.domain.Board;
import com.trip.triptogether.domain.Comment;
import com.trip.triptogether.domain.User;
import com.trip.triptogether.dto.request.comment.CommentReqeust;
import com.trip.triptogether.dto.request.comment.CommentRequestMapper;
import com.trip.triptogether.dto.response.CommentResponse;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.repository.board.BoardRepository;
import com.trip.triptogether.repository.comment.CommentRepository;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final BoardRepository boardRepository;
    private final CommentRequestMapper commentRequestMapper;
    private final ResponseService responseService;

    //댓글 등록
    @Transactional
    public CommonResponse createComment(Long boardId, CommentReqeust commentReqeust){
        User user=securityUtil.getAuthUserOrThrow();
        User chkUser = userRepository.findById(user.getId()).orElseThrow(
                () -> new NoSuchElementException("user not found"));

        Board board=boardRepository.findById(boardId).orElseThrow(()-> new NotFoundException("Could not found board id"));

        //정확히 하는 역할? entity 변환 아닌가?
        Comment comment=commentRequestMapper.toEntity(commentReqeust);
        Comment parentComment;
        //부모 댓글이 없을 경우
        if(commentReqeust.getParentId()!=null){
            parentComment= commentRepository.findById(commentReqeust.getParentId())
                    .orElseThrow(()->new NotFoundException("Could not found comment id : " + commentReqeust.getParentId()));
            comment.addParent(parentComment);
        }
        comment.setWriter(user.getNickname());
        comment.setBoard(board);
        comment.setContent(commentReqeust.getContent());
        comment.setUser(user);

        commentRepository.save(comment);
        user.getCommentList().add(comment);
        return responseService.getSingleResponse(HttpStatus.OK.value(), new CommentResponse(comment.getId(),comment.getContent(),comment.getWriter()));
    }

    //댓글 수정
    @Transactional
    public CommonResponse updateComment(Long commentId, CommentReqeust commentReqeust) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Could not found comment id : " + commentId));
        //작성자가 아닐 경우
        if(!checkCommentLoginUser(comment)) {
            return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(), "do not have permission to edit comments.");
        }
        comment.updateContent(commentReqeust.getContent());
        return responseService.getGeneralResponse(HttpStatus.OK.value(),"success update comment content");
    }

    //댓글 삭제
    @Transactional
    public CommonResponse deleteComment(Long commentId) {

        Comment comment = commentRepository.findCommentByIdWithParent(commentId)
                .orElseThrow(() -> new NotFoundException("Could not found comment id : " + commentId));
        //작성자가 아닐 경우
        if(!checkCommentLoginUser(comment)) {
            return responseService.getGeneralResponse(HttpStatus.BAD_REQUEST.value(), "do not have permission to edit comments.");
        }
        else {
            //자식이 있을 경우 -> 상태만 변경
            if (comment.getChildren().size() != 0) {
                comment.changeIsDeleted(true);
            }
            //자식이 없을 경우 (==대댓글이 없을 경우) -> 바로 삭제
            commentRepository.delete(getDeletableAncestorComment(comment));
        }

        return responseService.getGeneralResponse(HttpStatus.OK.value(),"success delete comment");
    }

    private Comment getDeletableAncestorComment(Comment comment) {
        Comment parent = comment.getParent(); // 현재 댓글의 부모를 구함
        if(parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted())
            // 부모가 있고, 부모의 자식이 1개(지금 삭제하는 댓글)이고, 부모의 삭제 상태가 TRUE인 댓글이라면 재귀
            return getDeletableAncestorComment(parent);
        return comment; // 삭제해야하는 댓글 반환
    }



    //수정 및 삭제 권한 체크
    private boolean checkCommentLoginUser(Comment comment) {
        User user=securityUtil.getAuthUserOrThrow();
        User chkUser=userRepository.findById(user.getId())
                .orElseThrow(()->new UsernameNotFoundException("could not found user"));
        if (!Objects.equals(comment.getWriter(), chkUser.getNickname())) {
            return false;
        }
        return true;

    }
}
