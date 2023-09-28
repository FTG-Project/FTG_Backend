package com.trip.triptogether.repository.chat;

import com.trip.triptogether.domain.Room;
import com.trip.triptogether.domain.User;
import com.trip.triptogether.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
class RoomRepositoryImplTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void findRoomByOwnerAndVisitor() {
        //given
        User user1 = User.builder().nickname("qwer").build();
        User user2 = User.builder().nickname("asdf").build();
        User user3 = User.builder().nickname("zxcv").build();
        User user4 = User.builder().nickname("tyui").build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);

        // (1-2, 1-3, 2-3)
        Room room1 = Room.builder().owner(user1).visitor(user2).build();
        Room room2 = Room.builder().owner(user3).visitor(user1).build();
        Room room3 = Room.builder().owner(user2).visitor(user3).build();
        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);

        //when
        Room findRoom1 = roomRepository.findRoomByOwnerAndVisitor(user1.getId(), user2.getId()).get();
        Room findRoom2 = roomRepository.findRoomByOwnerAndVisitor(user2.getId(), user1.getId()).get();

        Room findRoom3 = roomRepository.findRoomByOwnerAndVisitor(user2.getId(), user3.getId()).get();
        Optional<Room> findRoom4 = roomRepository.findRoomByOwnerAndVisitor(user1.getId(), user4.getId());

        //then
        Assertions.assertThat(findRoom1).isEqualTo(room1);
        Assertions.assertThat(findRoom2).isEqualTo(room1);
        Assertions.assertThat(findRoom3).isEqualTo(room3);
        Assertions.assertThat(findRoom4.isEmpty()).isEqualTo(true);
    }

    @Test
    void findRoomsByUserId() {
        //given
        User user1 = User.builder().nickname("qwer").build();
        User user2 = User.builder().nickname("asdf").build();
        User user3 = User.builder().nickname("zxcv").build();
        User user4 = User.builder().nickname("tyui").build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);

        // (1-2, 1-3, 2-3)
        Room room1 = Room.builder().owner(user1).visitor(user2).build();
        Room room2 = Room.builder().owner(user3).visitor(user1).build();
        Room room3 = Room.builder().owner(user2).visitor(user3).build();
        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);

        PageRequest pageRequest = PageRequest.of(0, 3);

        //when
        Page<Room> findRooms = roomRepository.findRoomsByUserId(user1.getId(), pageRequest);

        //then
        Assertions.assertThat(findRooms.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findByIdWithUsers() {

    }
}