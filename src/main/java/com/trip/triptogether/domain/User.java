package com.trip.triptogether.domain;

import com.trip.triptogether.constant.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String nickname;

    private String email;

    private String socialId;

    private String profileImage;

    private String language;

    private String refreshToken;

    // 최초 로그인 구분, GUEST, USER
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Board> boardList=new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<RecommendLikes> recommendLikesList = new ArrayList<>();

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }


    //Sign up
    public void signUp(String nickname, String language) {
        this.nickname = nickname;
        this.language = language;
        this.role = Role.USER;
    }

    public void logout() {
        this.refreshToken = null;
    }
}


