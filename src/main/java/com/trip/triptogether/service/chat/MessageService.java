package com.trip.triptogether.service.chat;

import com.trip.triptogether.constant.MessageType;
import com.trip.triptogether.domain.Message;
import com.trip.triptogether.domain.Room;
import com.trip.triptogether.domain.User;
import com.trip.triptogether.dto.request.chat.MessageCache;
import com.trip.triptogether.dto.request.chat.MessageRequest;
import com.trip.triptogether.dto.response.chat.MessageResponse;
import com.trip.triptogether.repository.chat.MessageRepository;
import com.trip.triptogether.repository.chat.RoomRepository;
import com.trip.triptogether.repository.user.UserRepository;
import com.trip.triptogether.util.RedisUtil;
import com.trip.triptogether.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final RedisUtil redisUtil;

    private final int maximumQueueSize = 10; // 30
    private final int commitSize = 5; // 20
    private final int pageSize = 8; // 10

    @Transactional
    public void createMessage(MessageRequest messageRequest) {
        roomRepository.findById(messageRequest.getRoomId()).orElseThrow(
                () -> new IllegalStateException("Room not found"));

        User chatUser = securityUtil.getAuthUserOrThrow();

        MessageCache messageCache = MessageCache.builder()
                .content(messageRequest.getContent())
                .chatUserId(chatUser.getId())
                .createdAt(LocalDateTime.now())
                .build();

        // 채팅방에 최초 입장 처리
        if (messageRequest.getType() == MessageType.ENTER) {
            messageCache.setContent(chatUser.getNickname() + "님이 입장했습니다.");
        }

        if (!redisUtil.containKey(messageRequest.getRoomId())) {
            Queue<MessageCache> messageCacheQueue = new LinkedList<>();
            messageCacheQueue.add(messageCache);

            redisUtil.putMessage(messageRequest.getRoomId(), messageCacheQueue);
            redisUtil.putDummyMessage(messageRequest.getRoomId());
        } else {
            Queue<MessageCache> messageCacheQueue = redisUtil.getMessage(messageRequest.getRoomId());
            messageCacheQueue.add(messageCache);

            if (messageCacheQueue.size() > maximumQueueSize) {
                Queue<MessageCache> queue = new LinkedList<>();
                for (int i = 0; i < commitSize; i++) {
                    queue.add(messageCacheQueue.poll());
                }
                commitMessage(queue, messageRequest.getRoomId());
            }

            redisUtil.putMessage(messageRequest.getRoomId(), messageCacheQueue);
            redisUtil.putDummyMessage(messageRequest.getRoomId());
        }
    }

    @Transactional
    public void commitMessage(Queue<MessageCache> queue, Long roomId) {
        for (int i = 0; i < commitSize; i++) {
            MessageCache messageCache = queue.poll();

            User chatUser = userRepository.findById(messageCache.getChatUserId()).orElseThrow(
                    () -> new IllegalStateException("User not found"));
            Room room = roomRepository.findById(roomId).orElseThrow(
                    () -> new IllegalStateException("Room not found"));

            Message message = Message.builder()
                    .chatUser(chatUser)
                    .room(room)
                    .content(messageCache.getContent())
                    .createdDate(messageCache.getCreatedAt())
                    .build();

            messageRepository.save(message);
        }
    }

    public List<MessageResponse> findMessages(Pageable pageable, Long roomId) {
        Room room = roomRepository.findByIdWithUsers(roomId).orElseThrow(
                () -> new IllegalStateException("Room not found"));

        List<MessageResponse> messageResponses = new ArrayList<>();

        //cache miss
        if (!redisUtil.containKey(roomId)) {
            List<Message> messages = messageRepository.findByRoomIdReverse(roomId, pageable);
            // DB 에도 값이 없는 케이스
            if (messages.isEmpty()) return messageResponses;

            //redis 에 캐싱할 값 setting
            List<MessageCache> messageCaches = messages.stream()
                    .map(m -> new MessageCache(m.getContent(),
                            m.getCreatedDate(), m.getChatUser().getId()))
                    .collect(Collectors.toList());

            Queue<MessageCache> messageCacheQueue = new LinkedList<>(messageCaches);
            redisUtil.putMessage(roomId, messageCacheQueue);

            //response 값 setting
            messageResponses = messages.stream()
                    .map(m -> new MessageResponse(m.getContent(),
                            m.getChatUser().getNickname(),
                            m.getCreatedDate(),
                            m.getChatUser().getProfileImage()))
                    .collect(Collectors.toList());
        } else { //cache hit
            String ownerName = room.getOwner().getNickname();
            String ownerImage = room.getOwner().getProfileImage();
            String visitorName = room.getVisitor().getNickname();
            String visitorImage = room.getVisitor().getProfileImage();

            LinkedList<MessageCache> messageCaches = redisUtil.getMessage(roomId);

            //시작 조회 위치
            int start = pageable.getPageNumber() * pageSize;

            //범위 초과
            if (start > messageCaches.size() - 1) {
                return messageResponses;
            } else {
                for (int i = start; i < start + pageSize; i++) {
                    //cache 초과
                    if(i > messageCaches.size() - 1) {
                        if (!messageResponses.isEmpty()) {
                            Collections.reverse(messageResponses);
                        }
                        //남은 pageSize 만큼 DB 에서 조회
                        PageRequest pageRequest = PageRequest.of(0, pageSize - (i - start));
                        List<Message> messages = messageRepository.findByRoomIdReverse(roomId, pageRequest);

                        for (Message message : messages) {
                            messageResponses.add(new MessageResponse(message.getContent(),
                                    message.getChatUser().getNickname(),
                                    message.getCreatedDate(),
                                    message.getChatUser().getProfileImage()));
                        }

                        break;
                    }

                    MessageCache messageCache = messageCaches.get(i);

                    MessageResponse messageResponse = null;
                    if (messageCache.getChatUserId() == room.getOwner().getId()) {
                        messageResponse = getMessageResponse(ownerName, ownerImage, messageCache);
                    } else {
                        messageResponse = getMessageResponse(visitorName, visitorImage, messageCache);
                    }

                    messageResponses.add(messageResponse);
                }
            }
        }
        return messageResponses;
    }

    private MessageResponse getMessageResponse(String writer, String image, MessageCache messageCache) {
        return MessageResponse.builder()
                .content(messageCache.getContent())
                .createdAt(messageCache.getCreatedAt())
                .imageUrl(image)
                .writer(writer)
                .build();
    }
}
