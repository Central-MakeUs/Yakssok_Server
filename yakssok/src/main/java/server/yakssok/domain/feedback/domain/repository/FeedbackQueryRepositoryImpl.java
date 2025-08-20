package server.yakssok.domain.feedback.domain.repository;

import static server.yakssok.domain.feedback.domain.entity.QFeedback.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.feedback.domain.entity.FeedbackType;

@RequiredArgsConstructor
public class FeedbackQueryRepositoryImpl implements FeedbackQueryRepository{
	private final JPAQueryFactory queryFactory;

	@Override
	public Map<Long, LocalDateTime> findTodayLastNagTimeToFollowings(Long userId, List<Long> followingIds, LocalDate today) {
		LocalDateTime start = today.atStartOfDay();
		LocalDateTime end   = start.plusDays(1);

		List<Tuple> rows = queryFactory
			.select(feedback.receiver.id, feedback.createdAt.max())
			.from(feedback)
			.where(
				feedback.sender.id.eq(userId),
				feedback.receiver.id.in(followingIds),
				feedback.feedbackType.eq(FeedbackType.NAG),
				feedback.createdAt.goe(start).and(feedback.createdAt.lt(end))
			)
			.groupBy(feedback.receiver.id)
			.fetch();

		Map<Long, LocalDateTime> result = new HashMap<>();
		for (Tuple t : rows) {
			result.put(t.get(feedback.receiver.id), t.get(feedback.createdAt.max()));
		}
		return result;
	}

	@Override
	public List<Long> findPraisedUserIdsOnDate(Long userId, List<Long> followingIds, LocalDate today) {
		return queryFactory
			.select(feedback.receiver.id)
			.from(feedback)
			.where(
				feedback.sender.id.eq(userId),
				feedback.receiver.id.in(followingIds),
				feedback.feedbackType.eq(FeedbackType.PRAISE),
				feedback.createdAt.goe(today.atStartOfDay())
			)
			.distinct()
			.fetch();
	}
}
