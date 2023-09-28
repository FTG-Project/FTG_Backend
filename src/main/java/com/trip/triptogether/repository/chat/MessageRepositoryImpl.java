package com.trip.triptogether.repository.chat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trip.triptogether.domain.Message;
import com.trip.triptogether.domain.QMessage;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.trip.triptogether.domain.QMessage.message;

public class MessageRepositoryImpl implements MessageRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public MessageRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Message> findByRoomIdReverse(Long roomId, Pageable pageable) {
        return queryFactory
                .selectFrom(message)
                .innerJoin(message.chatUser).fetchJoin()
                .where(message.room.id.eq(roomId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(message.id.desc())
                .fetch();
    }
}
