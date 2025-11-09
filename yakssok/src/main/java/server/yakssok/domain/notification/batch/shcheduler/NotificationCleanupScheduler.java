package server.yakssok.domain.notification.batch.shcheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.batch.job.NotificationCleanupJob;

@Component
@RequiredArgsConstructor
public class NotificationCleanupScheduler {

	private final NotificationCleanupJob notificationCleanupJob;

	/**
	 * 매일 새벽 3시에 31일 이전 알림 삭제
	 **/
	@Scheduled(cron = "0 0 3 * * *")
	public void cleanupOldNotifications() {
		long deletedCount = notificationCleanupJob.deleteOldNotifications();
		System.out.println("[NotificationCleanupScheduler] deleted old notifications: " + deletedCount);
	}
}
