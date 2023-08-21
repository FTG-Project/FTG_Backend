package com.trip.triptogether.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trip.triptogether.domain.Board;
import com.trip.triptogether.domain.Comment;
import com.trip.triptogether.domain.Photo;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BoardResponse {
    private PageResponse pageResponse;
    private String title; //제목
    private String contents; //내용
    private String writer;  // 작성자
    private int viewCount; //조회수
    private List<String> photoList; //이미지 리스트
    private List<CommentResponse> commentList; //댓글 리스트

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDate; //패턴 추후 변경
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedDate;


    public BoardResponse(Board board) {
        this.writer = board.getWriter();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.viewCount = board.getView_cnt();
        this.createdDate = board.getCreatedDate();
        this.updatedDate=board.getUpdatedDate();
        this.photoList = new ArrayList<>();
        List<Photo> photoList = board.getPhotoList();
        if (photoList != null) {
            for (Photo photo : photoList) {
                String fileUrl  = photo.getSavedFile();
                if (fileUrl != null) {
                    this.photoList.add(fileUrl);
                }
            }
        }

        this.commentList = new ArrayList<>();
        List<Comment> commentList = board.getCommentList();
        if (commentList != null) {
            for (Comment comment : commentList) {
                if (comment.getParent() == null) {
                    CommentResponse commentResponse = mapCommentToResponse(comment);
                    this.commentList.add(commentResponse);
                }
            }
        }
    }



    private CommentResponse mapCommentToResponse(Comment comment) {
        CommentResponse commentResponse = new CommentResponse(
                comment.getId(),
                comment.getWriter(), // 필드에 따라 업데이트
                comment.getContent()
        );

        for (Comment childComment : comment.getChildren()) {
            CommentResponse childCommentResponse = mapCommentToResponse(childComment);
            commentResponse.getChildren().add(childCommentResponse);
        }

        return commentResponse;
    }

    //Board 게시판 목록 응답
    @Getter
    public static class PageResponse {
        private String pageTitle; //제목
        private String pageContent; // 내용 , 이건 화면에 따라 뺼 수도 있을 듯
        private String writer; //작성자
        private int viewCount;
        private int commentCount;
        //생성 시간도 추가?

        public PageResponse(Board board) {
            this.pageTitle = board.getTitle(); //제목
            this.pageContent = board.getContents(); //내용
            this.writer= board.getWriter();; //작성자
            this.viewCount=board.getView_cnt(); //조회수
            this.commentCount=board.getCommentList().size(); //댓글 수
        }

        // Getter 및 Setter 메서드
    }


    }


