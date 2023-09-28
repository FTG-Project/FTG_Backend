package com.trip.triptogether.repository.chat;

import com.trip.triptogether.domain.Message;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageRepositoryCustom {
    List<Message> findByRoomIdReverse(Long roomId, Pageable pageable);
}
