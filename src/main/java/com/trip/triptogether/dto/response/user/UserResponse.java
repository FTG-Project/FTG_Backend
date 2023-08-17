package com.trip.triptogether.dto.response.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    // TODO : temp userResponse 스펙, 명세서 나오면 수정
    private Long userId;
    private String nickname;
    private String profileImage;
    private String email;
}
