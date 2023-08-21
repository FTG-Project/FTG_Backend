package com.trip.triptogether.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Integer view_cnt; //조회수

    @Column
    private String writer; //작성자

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
    @Enumerated(EnumType.STRING)
    private BoardType boardType; // 여행 카테고리

    public void addPhoto(Photo photo){
        photoList.add(photo);
        photo.addBoard(this);
    }



}
