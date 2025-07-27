package server.yakssok.domain.notification.domain.repository;

import java.util.List;

import server.yakssok.domain.notification.domain.entity.Notification;

public interface NotificationQueryRepository {
	List<Notification> findMyNotifications(Long userId);
}
