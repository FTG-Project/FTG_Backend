package com.trip.triptogether.service.friend;

import com.trip.triptogether.domain.Friend;
import com.trip.triptogether.domain.User;
import com.trip.triptogether.dto.response.user.UserResponse;
import com.trip.triptogether.repository.friend.FriendRepository;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;
    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;

    // request friendship
    @Transactional
    public void createFriendship(Long id) {
        User fromUser = securityUtil.getAuthUserOrThrow();
        User toUser = userRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("user not found"));

        //이미 친구관계이거나 친구 요청을 한 상태라면 exception
        friendRepository.findByFromToUser(fromUser.getId(), toUser.getId()).ifPresent(m -> {
                throw new IllegalStateException("this friendship already exist in db");
        });

        //forward record
        Friend forwardFriendship = Friend.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .areWeFriend(Boolean.TRUE)
                .build();

        //reverse record
        Friend reverseFriendship = Friend.builder()
                .fromUser(toUser)
                .toUser(fromUser)
                .areWeFriend(Boolean.FALSE)
                .build();

        friendRepository.save(forwardFriendship);
        friendRepository.save(reverseFriendship);
    }

    // 아직 상대방이 요청을 수락하지 않은 친구 조회
    public Page<UserResponse> findUserToUserNotYetAccept(Pageable pageable) {
        User user = securityUtil.getAuthUserOrThrow();
        return userRepository.findUserToUserNotYetAccept(user.getId(), pageable)
                .map(u -> new UserResponse(u.getId(), u.getNickname(), u.getProfileImage(), u.getEmail()));
    }

    // 내가 아직 친구 수락을 하지 않은 친구 조회
    public Page<UserResponse> findUserFromUserNotYetAccept(Pageable pageable) {
        User user = securityUtil.getAuthUserOrThrow();
        return userRepository.findUserFromUserNotYetAccept(user.getId(), pageable)
                .map(u -> new UserResponse(u.getId(), u.getNickname(), u.getProfileImage(), u.getEmail()));
    }

    // 친구 요청 수락
//    @Transactional
//    public void acceptFriendship(Long id) {
//        User user = securityUtil.getAuthUserOrThrow();
//        User toUser = userRepository.findById(id).orElseThrow(
//                () -> new NoSuchElementException("user not found"));
//
//        //a->b : true and b->a : false 인 친구를 찾아야 함.
//        //결과값 : b->a friendship
//        Friend findFriendship = friendRepository.findFriendNotYetAccept(user.getId(), toUser.getId()).orElseThrow(
//                () -> new IllegalStateException("doesn't exist friendship request in db"));
//
//        //Request accept -> update (areWeFriend = True) at reverse record
//        findFriendship.acceptFriendRequest();
//    }
}
