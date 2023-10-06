package com.trip.triptogether.controller;

import com.trip.triptogether.dto.request.FcmRequestDto;
import com.trip.triptogether.service.fcn.FCMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FcmController {
    private final FCMService fcmService;

    @PostMapping("/fcm")
    public ResponseEntity pushMessage(@RequestBody FcmRequestDto fcmRequestDto) throws IOException {
        System.out.println(fcmRequestDto.getTargetToken() + " "
                +fcmRequestDto.getTitle() + " " + fcmRequestDto.getBody());

        fcmService.sendMessageTo(
                fcmRequestDto.getTargetToken(),
                fcmRequestDto.getTitle(),
                fcmRequestDto.getBody());
        return ResponseEntity.ok().build();
    }
}
