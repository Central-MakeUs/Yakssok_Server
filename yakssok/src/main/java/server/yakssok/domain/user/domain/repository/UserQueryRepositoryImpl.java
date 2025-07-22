package server.yakssok.domain.user.domain.repository;


import static server.yakssok.domain.user.domain.entity.QUser.*;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.domain.user.domain.entity.User;

@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository{
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<User> findUserByProviderId(OAuthType oAuthType, String providerId) {
		User result = queryFactory
			.selectFrom(user)
			.where(
				user.oAuthType.eq(oAuthType),
				user.providerId.eq(providerId)
			)
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public boolean existsUserByProviderId(OAuthType oAuthType, String providerId) {
		return Optional.ofNullable(queryFactory
			.selectOne()
			.from(user)
			.where(
				user.oAuthType.eq(oAuthType),
				user.providerId.eq(providerId)
			)
			.fetchFirst()).isPresent();
	}
}
