package com.trip.triptogether.repository.chat;

import com.trip.triptogether.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RoomRepositoryCustom {
    Optional<Room> findRoomByOwnerAndVisitor(Long ownerId, Long visitorId);

    Page<Room> findRoomsByUserId(Long userId, Pageable pageable);

    public Optional<Room> findByIdWithUsers(Long roomId);
}
