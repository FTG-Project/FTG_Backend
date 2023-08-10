package com.trip.triptogether.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSaveDto {
    String nickname;
    String language;
}
