package com.trip.triptogether.repository.user;

import com.trip.triptogether.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    Page<User> findUserToUserNotYetAccept(Long fromUserId, Pageable pageable);
    Page<User> findUserFromUserNotYetAccept(Long fromUserId, Pageable pageable);
    Page<User> findFriends(Long fromUserId, Pageable pageable);

}
