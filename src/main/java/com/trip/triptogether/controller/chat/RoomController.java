package com.trip.triptogether.controller.chat;

import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.dto.response.chat.RoomResponse;
import com.trip.triptogether.service.chat.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    private final ResponseService responseService;

    @PostMapping("/{visitorId}")
    public CommonResponse.GeneralResponse createRoom(@PathVariable Long visitorId) {
        roomService.createRoom(visitorId);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "create room successfully");
    }

    @GetMapping
    public CommonResponse.SingleResponse<Page<RoomResponse>> findRooms(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                           Pageable pageable) {
        return responseService.getSingleResponse(HttpStatus.OK.value(), roomService.findRooms(pageable));
    }

    @DeleteMapping("/{roomId}")
    public CommonResponse.GeneralResponse deleteRoom(@PathVariable Long roomId) {
        roomService.delete(roomId);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "delete room successfully");
    }
}
