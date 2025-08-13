package server.yakssok.domain.friend.domain.repository;


import static server.yakssok.domain.feedback.domain.entity.QFeedback.feedback;
import static server.yakssok.domain.friend.domain.entity.QFriend.*;
import static server.yakssok.domain.user.domain.entity.QUser.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.feedback.domain.entity.FeedbackType;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.medication_schedule.domain.entity.QMedicationSchedule;

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
	public List<Friend> findMyFollowings(Long userId) {
		return queryFactory
			.selectFrom(friend)
			.join(friend.following).fetchJoin()
			.where(friend.user.id.eq(userId))
			.orderBy(friend.id.desc())
			.fetch();
	}

	@Override
	public List<Friend> findMyFollowers(Long userId) {
		return queryFactory
			.selectFrom(friend)
			.where(friend.following.id.eq(userId))
			.orderBy(friend.id.desc())
			.fetch();
	}

	@Override
	public Optional<Friend> findByUserIdAndFollowingId(Long userId, Long followingId) {
		return Optional.ofNullable(queryFactory
			.selectFrom(friend)
			.join(friend.following, user).fetchJoin()
			.where(
				friend.user.id.eq(userId),
				friend.following.id.eq(followingId)
			)
			.fetchOne());
	}

	@Override
	public List<Long> findPraiseCandidatesToday(Long userId, LocalDate today) {

		QMedicationSchedule msAny = new QMedicationSchedule("msAny");
		QMedicationSchedule msNt  = new QMedicationSchedule("msNt");
		LocalDateTime startOfDay = today.atStartOfDay();
		LocalDateTime endOfDay   = startOfDay.plusDays(1);

		return queryFactory
			.select(friend.following.id)
			.distinct()
			.from(friend)
			.where(
				friend.user.id.eq(userId),

				// 오늘 이미 내가 칭찬한 유저는 제외
				JPAExpressions.selectOne()
					.from(feedback)
					.where(
						feedback.sender.id.eq(userId),
						feedback.receiver.id.eq(friend.following.id),
						feedback.feedbackType.eq(FeedbackType.PRAISE),
						feedback.createdAt.goe(startOfDay).and(feedback.createdAt.lt(endOfDay))
					).notExists(),

				// 오늘 스케줄이 최소 1개는 있어야 함
				JPAExpressions.selectOne()
					.from(msAny)
					.where(
						msAny.userId.eq(friend.following.id),
						msAny.scheduledDate.eq(today)
					).exists(),

				// 오늘 미복용 스케줄이 하나도 없어야 함
				JPAExpressions.selectOne()
					.from(msNt)
					.where(
						msNt.userId.eq(friend.following.id),
						msNt.scheduledDate.eq(today),
						msNt.isTaken.isFalse()
					).notExists()
			)
			.fetch();
	}
}