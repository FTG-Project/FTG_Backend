package com.trip.triptogether.controller.friend;

import com.trip.triptogether.service.friend.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {
    private final FriendService friendService;

    @PostMapping
    public void createFriendship(@RequestParam Long id) {
        friendService.createFriendship(id);
    }
}
