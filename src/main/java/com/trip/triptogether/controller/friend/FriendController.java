package com.trip.triptogether.controller.friend;

import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.dto.response.user.UserResponse;
import com.trip.triptogether.service.friend.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {
    private final FriendService friendService;
    private final ResponseService responseService;

    @PostMapping("/{userId}")
    public CommonResponse.GeneralResponse createFriendship(@PathVariable Long userId) {
        friendService.createFriend(userId);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "Friend request completed");
    }

    //아직 상대방이 수락하지 않은 친구 요청 조회
    @GetMapping("/toUserNotAccept")
    public ResponseEntity<Page<UserResponse>> findUsersToUserNotYetAccept(Pageable pageable) {
        return ResponseEntity.ok().body(friendService.findUserToUserNotYetAccept(pageable));
    }

    //아직 내가 수락하지 않은 친구 요청 조회
    @GetMapping("/fromUserNotAccept")
    public ResponseEntity<Page<UserResponse>> findUsersFromUserNotYetAccept(Pageable pageable) {
        return ResponseEntity.ok().body(friendService.findUserFromUserNotYetAccept(pageable));
    }

    //친추 accept
    @PostMapping("/accept")
    public CommonResponse.GeneralResponse acceptFriend(@RequestParam Long userId) {
        friendService.acceptFriend(userId);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "Friend accept completed");
    }

    //친구 모두 조회
    @GetMapping
    public ResponseEntity<Page<UserResponse>> findFriends(Pageable pageable) {
        return ResponseEntity.ok().body(friendService.findFriends(pageable));
    }

    @DeleteMapping("/{userId}")
    public CommonResponse.GeneralResponse deleteFriend(@PathVariable Long userId) {
        friendService.deleteFriend(userId);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "Friend deletion complete");
    }
}
