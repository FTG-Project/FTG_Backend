package com.trip.triptogether.controller.friend;

import com.trip.triptogether.dto.response.CommonResponse;
import com.trip.triptogether.dto.response.ResponseService;
import com.trip.triptogether.dto.response.user.UserResponse;
import com.trip.triptogether.service.friend.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "친구 추가 api", description = "친구 추가 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "retrieve weather info successfully", content = @Content(schema = @Schema(implementation = CommonResponse.GeneralResponse.class)))})
    public CommonResponse.GeneralResponse createFriendship(@PathVariable Long userId) {
        friendService.createFriend(userId);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "Friend request completed");
    }

    //아직 상대방이 수락하지 않은 친구 요청 조회
    @GetMapping("/toUserNotAccept")
    @Operation(summary = "상대방이 친구 수락 아직 안한 친구 조회 api", description = "상대방이 친구수락을 하지 않은 친구 조회 api 입니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "retrieve toUserNotAccept friend request successfully", content = @Content(schema = @Schema(implementation = UserResponse.class)))})
    public ResponseEntity<Page<UserResponse>> findUsersToUserNotYetAccept(Pageable pageable) {
        return ResponseEntity.ok().body(friendService.findUserToUserNotYetAccept(pageable));
    }

    //아직 내가 수락하지 않은 친구 요청 조회
    @GetMapping("/fromUserNotAccept")
    @Operation(summary = "아직 내가 친구 수락하지 않은 친구 조회", description = "아직 친구수락을 하지 않은 사람 조회 api 입니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "retrieve fromUserNotAccept friend request successfully", content = @Content(schema = @Schema(implementation = UserResponse.class)))})
    public ResponseEntity<Page<UserResponse>> findUsersFromUserNotYetAccept(Pageable pageable) {
        return ResponseEntity.ok().body(friendService.findUserFromUserNotYetAccept(pageable));
    }

    //친추 accept
    @PostMapping("/accept")
    @Operation(summary = "친구 수락 api", description = "친구 요청 수락 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "retrieve weather info successfully", content = @Content(schema = @Schema(implementation = CommonResponse.GeneralResponse.class)))})
    public CommonResponse.GeneralResponse acceptFriend(@RequestParam Long userId) {
        friendService.acceptFriend(userId);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "Friend accept completed");
    }

    //친구 모두 조회
    @GetMapping
    @Operation(summary = "친구를 모두 조회하는 api", description = "친구를 모두 조회하는 api 입니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "retrieve friends successfully", content = @Content(schema = @Schema(implementation = UserResponse.class)))})
    public ResponseEntity<Page<UserResponse>> findFriends(Pageable pageable) {
        return ResponseEntity.ok().body(friendService.findFriends(pageable));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "친구 삭제 api", description = "친구 삭제 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "delete friend successfully", content = @Content(schema = @Schema(implementation = CommonResponse.GeneralResponse.class)))})
    public CommonResponse.GeneralResponse deleteFriend(@PathVariable Long userId) {
        friendService.deleteFriend(userId);
        return responseService.getGeneralResponse(HttpStatus.OK.value(), "Friend deletion complete");
    }
}
