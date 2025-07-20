package server.yakssok.domain.friend.domain.repository;


import static server.yakssok.domain.friend.domain.entity.QFriend.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.domain.entity.Friend;

@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public boolean isAlreadyFollow(Long userId, Long friendId) {
		Integer fetchOne = queryFactory
			.selectOne()
			.from(friend)
			.where(
				friend.userId.eq(userId),
				friend.friendId.eq(friendId)
			)
			.fetchFirst();
		return fetchOne != null;
	}

	@Override
	public List<Friend> findFollowingsByUserId(Long userId) {
		return queryFactory
			.selectFrom(friend)
			.where(friend.userId.eq(userId))
			.orderBy(friend.id.desc())
			.fetch();
	}
}