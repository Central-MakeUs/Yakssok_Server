package server.yakssok.domain.notification.domain.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Slice;
import server.yakssok.domain.notification.domain.entity.Notification;

public interface NotificationQueryRepository {
	Slice<Notification> findMyNotifications(Long userId, Long cursorId, int limit);
	long deleteByCreatedAtBefore(LocalDateTime beforeBoundary);
}
