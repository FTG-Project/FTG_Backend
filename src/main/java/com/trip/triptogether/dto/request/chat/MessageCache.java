package com.trip.triptogether.dto.request.chat;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageCache {
    private String content;
    private LocalDateTime createdAt;
    private Long chatUserId;
}
