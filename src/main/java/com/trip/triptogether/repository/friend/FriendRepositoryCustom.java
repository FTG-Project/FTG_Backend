package com.trip.triptogether.repository.friend;

import com.trip.triptogether.domain.Friend;

import java.util.List;
import java.util.Optional;

public interface FriendRepositoryCustom {
    List<Friend> findFriendByFromToUser(Long fromUserId, Long toUserId);
    List<Friend> findByFromToUser(Long fromUserId, Long toUserId);
    Optional<Friend> findFriendNotYetAccept(Long fromUserId, Long toUserId);
}
