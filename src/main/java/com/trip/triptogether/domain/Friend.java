package com.trip.triptogether.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Friend {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
    private User toUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @Column(name = "are_we_friend")
    private Boolean areWeFriend;
}
