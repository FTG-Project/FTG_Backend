package com.trip.triptogether.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class RecommendLikes {
    @Id @GeneratedValue
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommend_id")
    Recommend recommend;
}
