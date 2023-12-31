package com.trip.triptogether.dto.response.board;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trip.triptogether.domain.Board;
import com.trip.triptogether.domain.Comment;
import com.trip.triptogether.dto.response.CommentResponse;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BoardResponse {
    private PageResponse pageResponse;
    private String title; // 제목
    private String contents; // 내용
    private String writer; // 작성자
    private String profileImage; // 프로필 사진
    private int viewCount; // 조회수
    private int likeCount; //좋아요 수
    private List<String> photoList; // 이미지 리스트

    private List<CommentResponse> commentList; // 댓글 리스트

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime updatedDate;


    public BoardResponse(Board board) {
        this.writer = board.getWriter();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.profileImage=board.getProfileImage();
        this.viewCount = board.getView_cnt();
        this.likeCount=board.getLikecnt();
        this.createdDate = board.getCreatedDate();
        this.updatedDate = board.getUpdatedDate();

        this.photoList = board.getPhotoList()
                .stream()
                .map(photo -> photo.getSavedFile())
                .filter(fileUrl -> fileUrl != null)
                .collect(Collectors.toList());

        this.commentList = board.getCommentList()
                .stream()
                .filter(comment -> comment.getParent() == null)
                .map(this::mapCommentToResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapCommentToResponse(Comment comment) {
        CommentResponse commentResponse = new CommentResponse(
                comment.getId(),
                comment.getWriter(),
                comment.getContent()
        );

        commentResponse.setChildren(comment.getChildren()
                .stream()
                .map(this::mapCommentToResponse)
                .collect(Collectors.toList())
        );

        return commentResponse;
    }

    @Getter
    public static class PageResponse {
        private String pageTitle; // 제목
        private String pageContent; // 내용 , 이건 화면에 따라 뺼 수도 있을 듯
        private String writer; // 작성자
        private int viewCount; //조회 수

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDateTime createdDate; // 패턴 추후 변경



        public PageResponse(Board board) {
            this.pageTitle = board.getTitle();
            this.pageContent = board.getContents();
            this.writer = board.getWriter();
            this.viewCount = board.getView_cnt();
            this.createdDate = board.getCreatedDate();
        }
    }
}
