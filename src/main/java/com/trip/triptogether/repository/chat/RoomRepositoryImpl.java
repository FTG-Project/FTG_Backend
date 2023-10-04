package com.trip.triptogether.repository.chat;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trip.triptogether.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static com.trip.triptogether.domain.QRoom.room;

public class RoomRepositoryImpl implements RoomRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public RoomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<Room> findRoomByOwnerAndVisitor(Long ownerId, Long visitorId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(room)
                .where((room.owner.id.eq(ownerId).and(room.visitor.id.eq(visitorId)))
                        .or(room.owner.id.eq(visitorId).and(room.visitor.id.eq(ownerId))))
                .fetchOne());
    }

    @Override
    public Page<Room> findRoomsByUserId(Long userId, Pageable pageable) {
        List<Room> content = queryFactory
                .selectFrom(room)
                .innerJoin(room.owner).fetchJoin()
                .innerJoin(room.visitor).fetchJoin()
                .where(room.owner.id.eq(userId).or(room.visitor.id.eq(userId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(room.count())
                .from(room)
                .where(room.owner.id.eq(userId).or(room.visitor.id.eq(userId)));


        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    public Optional<Room> findByIdWithUsers(Long roomId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(room)
                .innerJoin(room.owner).fetchJoin()
                .innerJoin(room.visitor).fetchJoin()
                .where(room.id.eq(roomId))
                .fetchOne());
    }
}
