package server.yakssok.domain.notification.domain.repository;

import org.springframework.data.domain.Slice;

import server.yakssok.domain.notification.domain.entity.Notification;

public interface NotificationQueryRepository {
	Slice<Notification> findMyNotifications(Long userId, Long cursorId, int limit);
}
