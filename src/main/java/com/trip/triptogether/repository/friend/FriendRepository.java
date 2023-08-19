package com.trip.triptogether.repository.friend;

import com.trip.triptogether.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("select f from Friend f" +
            " where (f.fromUser.id = :fromUserId and f.toUser.id = :toUserId)" +
            " or (f.fromUser.id = :toUserId and f.toUser.id = :fromUserId)")
    List<Friend> findByFromToUser(Long fromUserId, Long toUserId);


    //toUserId -> 수락할 친구, fromUserId -> 나
    @Query("select f from Friend f" +
            " where f.fromUser.id = :fromUserId and f.toUser.id = :toUserId" +
            " and f.areWeFriend = FALSE")
    Optional<Friend> findFriendNotYetAccept(Long fromUserId, Long toUserId);
}
