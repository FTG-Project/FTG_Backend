package com.trip.triptogether.dto.request.chat;

import com.trip.triptogether.constant.MessageType;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private String content;
    private Long roomId;
    private MessageType type;
}
