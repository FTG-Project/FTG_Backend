package com.trip.triptogether.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;



@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Photo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    @Column
    private String originFile;

    @Column
    private String savedFile;

    @Column
    private Long fileSize;

    public Photo(String savedFile, Board board){
        this.savedFile=savedFile;
        this.board=board;
    }

    public void addBoard(Board board){
        this.board=board;
    }

}

