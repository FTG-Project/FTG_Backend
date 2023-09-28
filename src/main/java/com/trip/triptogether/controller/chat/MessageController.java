package com.trip.triptogether.controller.chat;

import com.trip.triptogether.dto.request.chat.MessageRequest;
import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.dto.response.chat.MessageResponse;
import com.trip.triptogether.service.chat.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;
    private final ResponseService responseService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("chat/message")
    public void sendMessage(MessageRequest messageRequest) {
        simpMessagingTemplate.convertAndSend("/sub/chat/room" + messageRequest.getRoomId(), messageRequest);
        messageService.createMessage(messageRequest);
    }

    @GetMapping("/{roomId}")
    public CommonResponse.ListResponse<MessageResponse> findMessage(@PageableDefault Pageable pageable,
                                                                            @PathVariable Long roomId) {
        return responseService.getListResponse(HttpStatus.OK.value(), messageService.findMessages(pageable, roomId));
    }
}
