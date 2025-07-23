package server.yakssok.domain.notification.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.repository.NotificationRepository;
import server.yakssok.domain.notification.presentation.dto.NotificationRequest;
import server.yakssok.global.infra.fcm.FcmService;

@Service
@RequiredArgsConstructor
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final FcmService fcmService;

	@Transactional
	public void createNotification(NotificationRequest notificationRequest) {
		saveNotification(notificationRequest);
		pushNotification(notificationRequest);
	}

	private void pushNotification(NotificationRequest notificationRequest) {
		String fcmToken = notificationRequest.fcmToken();
		String title = notificationRequest.title();
		String body = notificationRequest.body();
		fcmService.sendNotification(fcmToken, title, body);
	}

	private void saveNotification(NotificationRequest notificationRequest) {
		Notification notification = notificationRequest.toNotification();
		notificationRepository.save(notification);
	}
}
