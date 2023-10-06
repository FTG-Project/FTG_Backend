package com.trip.triptogether.domain;


import com.trip.triptogether.dto.request.board.BoardRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Board extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id ;

    @Column
    private String title; //제목

    @Column
    private String contents; //내용

    @Column
    private String profileImage; //프로필 이미지
    @ColumnDefault("0")
    @Column
    private int view_cnt; //조회수

    @Column
    private String writer; //작성자

    @Enumerated(EnumType.STRING)
    private BoardType boardType; // 여행 카테고리

    //user 다대일 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "board")
    @Builder.Default
    private List<Photo> photoList=new ArrayList<>();

    @OneToMany(mappedBy = "board")
    @Builder.Default
    private List<Comment> commentList=new ArrayList<>();

    @ColumnDefault("0")
    @Column
    private int likecnt;


    public void updateBoard(BoardRequest boardRequest){
        this.title=boardRequest.getTitle();
        this.contents=boardRequest.getContent();
        this.boardType=boardRequest.getType();

    }


    public void addPhotoList(List<Photo> photo){
        this.photoList=photo;
    }

    public void increaseView() {
        this.view_cnt+=1;
    }

    public void updateView() {
        this.view_cnt++;
    }

    public void updatePhotoList(List<Photo> existingPhotos) {
        this.photoList=existingPhotos;
    }
}


