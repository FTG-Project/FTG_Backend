package com.trip.triptogether.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSaveRequest {
    @NotBlank(message = "nickname is essential")
    String nickname;
    @NotBlank(message = "language is essential")
    String language;
}
