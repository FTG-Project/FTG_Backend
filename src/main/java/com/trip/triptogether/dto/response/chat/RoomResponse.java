package com.trip.triptogether.dto.response.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponse {
    private Long roomId;
    private String visitorName;
    private String lastMessage;
}
