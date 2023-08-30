package com.trip.triptogether.repository.like;


import com.trip.triptogether.domain.Board;
import com.trip.triptogether.domain.Likes;
import com.trip.triptogether.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes,Long> {

    Optional<Likes> findByUsersAndBoard(User user, Board board);

}
