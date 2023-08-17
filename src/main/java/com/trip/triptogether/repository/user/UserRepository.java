package com.trip.triptogether.repository.user;

import com.trip.triptogether.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByRefreshToken(String refreshToken);
    Optional<User> findBySocialId(String socialId);

    //아직 친추 수락하지 않은 유저 조회
    @Query("select u from Friend f join User u on f.fromUser.id = :fromUserId" +
    " where f.areWeFriend = FALSE")
    Page<User> findUserToUserNotYetAccept(Long fromUserId, Pageable pageable);

    @Query("select u from Friend f join User u on f.toUser.id = :toUserId" +
    " where f.areWeFriend = FALSE")
    Page<User> findUserFromUserNotYetAccept(Long toUserId, Pageable pageable);
}
