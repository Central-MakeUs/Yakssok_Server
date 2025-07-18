package server.yakssok.domain.friend.domain.repository;

import static server.yakssok.domain.friend.domain.entity.QFriend.*;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public boolean isAlreadyFriend(Long userId, Long followingFriendId) {
		Integer fetchOne = queryFactory
			.selectOne()
			.from(friend)
			.where(
				friend.userId.eq(userId),
				friend.friendId.eq(followingFriendId)
			)
			.fetchFirst();
		return fetchOne != null;
	}
}