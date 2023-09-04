package com.trip.triptogether.repository.friend;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trip.triptogether.domain.Friend;
import com.trip.triptogether.domain.QFriend;

import java.util.List;
import java.util.Optional;

import static com.trip.triptogether.domain.QFriend.friend;

public class FriendRepositoryImpl implements FriendRepositoryCustom{

    private JPAQueryFactory queryFactory;

    public FriendRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Friend> findFriendByFromToUser(Long fromUserId, Long toUserId) {
        QFriend ff = new QFriend("ff");

        return queryFactory
                .selectFrom(friend)
                .innerJoin(ff).on(friend.toUser.id.eq(ff.fromUser.id))
                .where((friend.fromUser.id.eq(fromUserId).and(friend.toUser.id.eq(toUserId))
                                .and(friend.areWeFriend.eq(Boolean.TRUE)).and(ff.areWeFriend.eq(Boolean.TRUE)))
                                .or(friend.fromUser.id.eq(toUserId).and(friend.toUser.id.eq(fromUserId))
                                        .and(friend.areWeFriend.eq(Boolean.TRUE).and(ff.areWeFriend.eq(Boolean.TRUE)))))
                .fetch();
    }

    @Override
    public List<Friend> findByFromToUser(Long fromUserId, Long toUserId) {
        return queryFactory
                .selectFrom(friend)
                .where((friend.fromUser.id.eq(fromUserId).and(friend.toUser.id.eq(toUserId)))
                        .or(friend.toUser.id.eq(fromUserId).and(friend.fromUser.id.eq(toUserId))))
                .fetch();
    }

    @Override
    public Optional<Friend> findFriendNotYetAccept(Long fromUserId, Long toUserId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(friend)
                .where(friend.fromUser.id.eq(fromUserId),
                        friend.toUser.id.eq(toUserId),
                        friend.areWeFriend.eq(Boolean.FALSE))
                .fetchOne());
    }
}
