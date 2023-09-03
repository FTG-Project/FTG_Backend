package com.trip.triptogether.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert //동적 삽입에 대해
public class Comment extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //댓글 id
    @Column
    private String writer; //작성자
    @Column
    private String content;  //댓글 내용

    @ColumnDefault("FALSE")
    @Column(nullable = false)
    private Boolean isDeleted; //삭제

    @ColumnDefault("FALSE")
    @Column
    private Boolean isPrivated; // 비밀 여부


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true) //부모 댓글 삭제 시 하위 댓글 모두 삭제
    private List<Comment> children =new ArrayList<>();

//    @Builder
//    private Comment(String writer,String content,User user,Board board){
//        this.writer=writer;
//        this.content=content;
//        this.user=user;
//        this.board=board;
//    }

    public Comment(String content){
        this.content=content;
    }

    public void updateContent(String content){this.content=content;}
    public void changeIsDeleted(Boolean isDeleted){
        this.isDeleted=isDeleted;
    }

    public void changeIsPrivated(Boolean isPrivated){
        this.isPrivated=isPrivated;
    }
    public void addParent(Comment parent){
        this.parent=parent;
    }

}
