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
public class ReviewPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="review_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Review review;

    @Column
    private String originFile;

    @Column
    private String savedFile;

    @Column
    private Long fileSize;

    public ReviewPhoto(String savedFile, Review review) {
        this.savedFile = savedFile;
        this.review = review;
    }
    public void addReview(Review review){
        this.review=review;
    }
}
