package com.trip.triptogether.controller.friend;

import com.trip.triptogether.dto.response.user.UserResponse;
import com.trip.triptogether.service.friend.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    //TODO : 임시 URL, 임시 Response -> 변경 필요
    //아직 상대방이 수락하지 않은 친구 요청 조회
    @GetMapping("/toUserNotAccept")
    public Page<UserResponse> findUsersToUserNotYetAccept(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return friendService.findUserToUserNotYetAccept(pageRequest);
    }

    //TODO : 임시 URL, 임시 Response -> 변경 필요
    //아직 내가 수락하지 않은 친구 요청 조회
    @GetMapping("/fromUserNotAccept")
    public Page<UserResponse> findUsersFromUserNotYetAccept(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return friendService.findUserFromUserNotYetAccept(pageRequest);
    }

    //TODO : 임시 URL, 임시 Response -> 변경 필요
    //친추 accept
    @PostMapping("/accept")
    public void acceptFriend(@RequestParam Long id) {
        friendService.acceptFriend(id);
    }

    //TODO : 임시 URL, 임시 Response -> 변경 필요
    //친구 모두 조회
    @GetMapping
    public Page<UserResponse> findFriends(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return friendService.findFriends(pageRequest);
    }

}
