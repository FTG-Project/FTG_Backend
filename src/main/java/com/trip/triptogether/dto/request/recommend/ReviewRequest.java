package com.trip.triptogether.dto.request.recommend;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequest {
    @NotBlank(message = "rating is essential")
    private Double rating;

    @NotBlank(message = "content is essential")
    private String content;
}
