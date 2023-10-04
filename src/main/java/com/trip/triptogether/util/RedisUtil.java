package com.trip.triptogether.util;

import com.trip.triptogether.dto.request.chat.MessageCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    @Value("${spring.data.redis.expire}")
    private int expireTime;

    private final RedisTemplate<String, LinkedList<MessageCache>> redisMessageTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    //로그아웃 유저 -> Access Token 블랙리스트 등록
    private final RedisTemplate<String, Object> redisBlackListTemplate;

    public void set(String key, Object o, Long milliseconds) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(o.getClass()));
        redisTemplate.opsForValue().set(key, o, milliseconds, TimeUnit.MILLISECONDS);
    }

    //value 가져오기
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    //delete key
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    //key 값이 있는지 check
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setBlackList(String key, Object o, Long milliseconds) {
        redisBlackListTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(o.getClass()));
        redisBlackListTemplate.opsForValue().set(key, o, milliseconds, TimeUnit.MILLISECONDS);
    }

    public Object getBlackList(String key) {
        return redisBlackListTemplate.opsForValue().get(key);
    }

    public boolean deleteBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.delete(key));
    }

    public boolean hasKeyBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.hasKey(key));
    }

    //chat
    public void putMessage(Long roomId, Queue<MessageCache> messageQueue) {
        redisMessageTemplate.opsForValue().set(roomId.toString(), new LinkedList<>(messageQueue));
    }

    public void putDummyMessage(Long roomId) {
        redisMessageTemplate.opsForValue().set("room" + roomId.toString(), new LinkedList<>());
        redisMessageTemplate.expire("room" + roomId.toString(), expireTime, TimeUnit.MINUTES);
    }

    public boolean containKey(Long roomId) {
        return Boolean.TRUE.equals(redisMessageTemplate.hasKey(roomId.toString()));
    }

    public LinkedList<MessageCache> getMessage(Long roomId) {
        return redisMessageTemplate.opsForValue().get(roomId.toString());
    }

    public void deleteKey(Long roomId) {
        redisMessageTemplate.delete(roomId.toString());
    }
}