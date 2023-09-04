package com.trip.triptogether.repository.user;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trip.triptogether.domain.QFriend;
import com.trip.triptogether.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
import static com.trip.triptogether.domain.QFriend.*;
import static com.trip.triptogether.domain.QUser.user;

public class UserRepositoryImpl implements UserRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public UserRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<User> findUserToUserNotYetAccept(Long fromUserId, Pageable pageable) {
        QFriend ff = new QFriend("ff");

        List<User> users = queryFactory
                .select(user)
                .from(friend)
                .innerJoin(friend.fromUser, user)
                .where(friend.in(selectFrom(ff)
                        .where(ff.toUser.id.eq(fromUserId),
                                ff.areWeFriend.eq(Boolean.FALSE))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(friend.count())
                .from(friend)
                .where(friend.toUser.id.eq(fromUserId),
                        friend.areWeFriend.eq(Boolean.FALSE));

        return PageableExecutionUtils.getPage(users, pageable, count::fetchOne);
    }

    @Override
    public Page<User> findUserFromUserNotYetAccept(Long fromUserId, Pageable pageable) {
        List<User> users = queryFactory
                .select(friend.toUser)
                .from(friend)
                .innerJoin(friend.fromUser, user)
                .where(user.id.eq(fromUserId),
                        friend.areWeFriend.eq(Boolean.FALSE))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(friend.count())
                .from(friend)
                .where(friend.fromUser.id.eq(fromUserId),
                        friend.areWeFriend.eq(Boolean.FALSE));

        return PageableExecutionUtils.getPage(users, pageable, count::fetchOne);
    }

    @Override
    public Page<User> findFriends(Long fromUserId, Pageable pageable) {
        QFriend ff = new QFriend("ff");

        List<User> users = queryFactory
                .select(friend.toUser)
                .from(friend)
                .innerJoin(ff).on(friend.toUser.id.eq(ff.fromUser.id))
                .where(friend.fromUser.id.eq(fromUserId),
                        friend.areWeFriend.eq(Boolean.TRUE),
                        ff.areWeFriend.eq(Boolean.TRUE))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(friend.toUser.count())
                .from(friend)
                .innerJoin(ff).on(friend.toUser.id.eq(ff.fromUser.id))
                .where(friend.fromUser.id.eq(fromUserId),
                        friend.areWeFriend.eq(Boolean.TRUE),
                        ff.areWeFriend.eq(Boolean.TRUE))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());

        return PageableExecutionUtils.getPage(users, pageable, count::fetchOne);
    }
}
