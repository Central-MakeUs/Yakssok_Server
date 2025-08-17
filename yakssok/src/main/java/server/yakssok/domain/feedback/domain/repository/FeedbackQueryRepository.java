package server.yakssok.domain.feedback.domain.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface FeedbackQueryRepository {
	Map<Long, LocalDateTime> findTodayLastNagTimeToFollowings(Long userId, List<Long> followingIds, LocalDate today);
	LocalDateTime findTodayLastNagTimeToFollowing(Long userId, Long followingId, LocalDate today);
}
