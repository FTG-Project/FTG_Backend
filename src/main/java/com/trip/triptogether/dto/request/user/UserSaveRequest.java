package com.trip.triptogether.dto.request.user;

import com.trip.triptogether.constant.Language;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSaveRequest {
    @NotBlank(message = "nickname is essential")
    private String nickname;
    private Language language;
}
