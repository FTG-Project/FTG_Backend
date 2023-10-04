package com.trip.triptogether.service.chat;

import com.trip.triptogether.domain.Room;
import com.trip.triptogether.domain.User;
import com.trip.triptogether.dto.response.chat.RoomResponse;
import com.trip.triptogether.repository.chat.RoomRepository;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoomService {

    private final SecurityUtil securityUtil;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createRoom(Long visitorId) {
        User owner = securityUtil.getAuthUserOrThrow();
        User visitor = userRepository.findById(visitorId).orElseThrow(
                () -> new NoSuchElementException("User not found"));

        // 이미 둘 사이에 채팅방이 있는 경우 체크
        if (roomRepository.findRoomByOwnerAndVisitor(owner.getId(), visitorId).isPresent()) {
            throw new IllegalStateException("already exist room");
        }

        Room room = Room.builder()
                .messages(new ArrayList<>())
                .owner(owner)
                .visitor(visitor)
                .build();

        roomRepository.save(room);
    }

    public Page<RoomResponse> findRooms(Pageable pageable) {
        User owner = securityUtil.getAuthUserOrThrow();
        Page<Room> findRooms = roomRepository.findRoomsByUserId(owner.getId(), pageable);
        List<RoomResponse> roomResponses = new ArrayList<>();

        List<Room> rooms = findRooms.getContent();
        for (Room room : rooms) {
            //TODO : add last Message
            if (room.getOwner().getId() == owner.getId()) {
                RoomResponse roomResponse = getRoomResponse(room, room.getVisitor());
                roomResponses.add(roomResponse);
            } else {
                RoomResponse roomResponse = getRoomResponse(room, room.getOwner());
                roomResponses.add(roomResponse);
            }
        }

        return new PageImpl<RoomResponse>(roomResponses, pageable, findRooms.getTotalElements());
    }

    @Transactional
    public void delete(Long roomId) {
        roomRepository.deleteById(roomId);
    }

    private RoomResponse getRoomResponse(Room room, User visitor) {
        RoomResponse roomResponse = RoomResponse.builder()
                .roomId(room.getId())
                .lastMessage(null)
                .visitorName(visitor.getNickname())
                .build();

        return roomResponse;
    }
}
