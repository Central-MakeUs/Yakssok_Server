package server.yakssok.domain.feeback.domain.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface FeedbackQueryRepository {
	List<Long> findPraisedToday(Long senderId, List<Long> targetIds, LocalDate today);
	Map<Long, LocalDateTime> findTodayLastNagTime(Long userId, List<Long> followingIds, LocalDate now);
}
