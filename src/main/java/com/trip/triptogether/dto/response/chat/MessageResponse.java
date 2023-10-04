package com.trip.triptogether.dto.response.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {

    private String content;
    private String writer;
    private LocalDateTime createdAt;
    private String imageUrl;
}
