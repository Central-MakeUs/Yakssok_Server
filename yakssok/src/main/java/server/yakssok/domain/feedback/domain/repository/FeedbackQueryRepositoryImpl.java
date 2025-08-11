package server.yakssok.domain.feedback.domain.repository;

import static server.yakssok.domain.feedback.domain.entity.QFeedback.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.feedback.domain.entity.FeedbackType;

@RequiredArgsConstructor
public class FeedbackQueryRepositoryImpl implements FeedbackQueryRepository{
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Long> findPraisedToday(Long senderId, List<Long> targetIds, LocalDate today) {
		return queryFactory
			.select(feedback.receiver.id)
			.from(feedback)
			.where(
				feedback.sender.id.eq(senderId),
				feedback.receiver.id.in(targetIds),
				feedback.feedbackType.eq(FeedbackType.PRAISE),
				feedback.createdAt.goe(today.atStartOfDay())
			)
			.distinct()
			.fetch();
	}

	@Override
	public Map<Long, LocalDateTime> findTodayLastNagTime(Long userId, List<Long> followingIds, LocalDate now) {
		LocalDateTime startOfDay = now.atStartOfDay();
		var maxCreated = feedback.createdAt.max();

		List<com.querydsl.core.Tuple> rows = queryFactory
			.select(feedback.receiver.id, maxCreated)
			.from(feedback)
			.where(
				feedback.sender.id.eq(userId),
				feedback.receiver.id.in(followingIds),
				feedback.feedbackType.eq(FeedbackType.NAG),
				feedback.createdAt.goe(startOfDay)
			)
			.groupBy(feedback.receiver.id)
			.fetch();
		Map<Long, LocalDateTime> result = new java.util.HashMap<>();
		for (var t : rows) {
			result.put(t.get(feedback.receiver.id), t.get(maxCreated));
		}
		return result;
	}
}
