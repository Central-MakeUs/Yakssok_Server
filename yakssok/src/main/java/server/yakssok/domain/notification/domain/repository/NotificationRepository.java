package server.yakssok.domain.notification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import server.yakssok.domain.notification.domain.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationQueryRepository{
}
