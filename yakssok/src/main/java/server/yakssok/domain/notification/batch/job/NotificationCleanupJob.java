package server.yakssok.domain.notification.batch.job;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.domain.repository.NotificationRepository;

@Component
@RequiredArgsConstructor
public class NotificationCleanupJob {
	private static final int RETENTION_DAYS = 31;
	private final NotificationRepository notificationRepository;

	/**
	 * 오늘 기준 31일 이전 알림 삭제
	 */
	@Transactional
	public long deleteOldNotifications() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime beforeBoundary = now.minusDays(RETENTION_DAYS);
		return notificationRepository.deleteByCreatedAtBefore(beforeBoundary);
	}
}
