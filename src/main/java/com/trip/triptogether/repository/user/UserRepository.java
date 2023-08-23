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

    //TODO : countQuery 최적화, Repository 분리

    //상대방이 아직 안받은 케이스
    @Query("select u from Friend f join User u on f.fromUser.id = u.id" +
        " where f in (select ff from Friend ff where ff.toUser.id = :fromUserId and ff.areWeFriend = FALSE)")
    Page<User> findUserToUserNotYetAccept(Long fromUserId, Pageable pageable);

    //내가 아직 안받은 케이스
    @Query("select f.toUser from Friend f join User u on f.fromUser.id = u.id" +
        " where f.fromUser.id = :fromUserId and f.areWeFriend = FALSE")
    Page<User> findUserFromUserNotYetAccept(Long fromUserId, Pageable pageable);

    //친구 모두 조회
    @Query("select f.toUser from Friend f join Friend ff on f.toUser.id = ff.fromUser.id" +
            " where f.fromUser.id = :fromUserId and" +
            " f.areWeFriend = TRUE and" +
            " ff.areWeFriend = TRUE")
    Page<User> findFriends(Long fromUserId, Pageable pageable);
}
