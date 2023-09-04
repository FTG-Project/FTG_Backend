package com.trip.triptogether.repository.friend;

import com.trip.triptogether.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>, FriendRepositoryCustom {
}
