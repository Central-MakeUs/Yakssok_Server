package server.yakssok.domain.notification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import server.yakssok.domain.notification.domain.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
