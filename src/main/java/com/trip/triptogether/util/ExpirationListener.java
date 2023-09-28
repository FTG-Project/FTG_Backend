package com.trip.triptogether.util;

import com.trip.triptogether.dto.request.chat.MessageCache;
import com.trip.triptogether.service.chat.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@RequiredArgsConstructor
@Component
@Slf4j
public class ExpirationListener implements MessageListener {

    private final RedisUtil redisUtil;
    private final MessageService messageService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("onMessage Key expiration {}", message.toString());
        Long roomId = Long.valueOf(message.toString().substring(4));
        LinkedList<MessageCache> messageCaches = redisUtil.getMessage(roomId);
        Queue<MessageCache> messageQueue = new LinkedList<>(messageCaches);

        messageService.commitMessage(messageQueue, roomId);

        redisUtil.deleteKey(roomId);
    }
}
