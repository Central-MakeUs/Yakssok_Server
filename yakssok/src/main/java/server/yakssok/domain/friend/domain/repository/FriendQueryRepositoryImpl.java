package server.yakssok.domain.friend.domain.repository;


import static server.yakssok.domain.friend.domain.entity.QFriend.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.domain.entity.Friend;

@RequiredArgsConstructor
public class FriendQueryRepositoryImpl implements FriendQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public boolean isAlreadyFollow(Long userId, Long followingId) {
		Integer fetchOne = queryFactory
			.selectOne()
			.from(friend)
			.where(
				friend.user.id.eq(userId),
				friend.following.id.eq(followingId)
			)
			.fetchFirst();
		return fetchOne != null;
	}

	@Override
	public List<Friend> findFollowingsByUserId(Long userId) {
		return queryFactory
			.selectFrom(friend)
			.join(friend.following).fetchJoin()
			.where(friend.user.id.eq(userId))
			.orderBy(friend.id.desc())
			.fetch();
	}
}