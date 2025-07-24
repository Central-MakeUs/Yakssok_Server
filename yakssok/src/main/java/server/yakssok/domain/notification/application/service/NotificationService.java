package server.yakssok.domain.notification.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.repository.NotificationRepository;
import server.yakssok.domain.notification.presentation.dto.NotificationRequest;
import server.yakssok.domain.user.domain.entity.UserDevice;
import server.yakssok.domain.user.domain.repository.UserDeviceRepository;
import server.yakssok.global.infra.fcm.FcmService;

@Service
@RequiredArgsConstructor
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final FcmService fcmService;
	private final UserDeviceRepository userDeviceRepository;

	@Transactional
	public void createNotification(NotificationRequest notificationRequest) {
		saveNotification(notificationRequest);
		pushNotification(notificationRequest);
	}

	private void pushNotification(NotificationRequest notificationRequest) {
		Long userId = notificationRequest.receiverId();
		List<String> tokens = userDeviceRepository.findByUserIdAndAlertOnTrue(userId)
			.stream()
			.map(UserDevice::getFcmToken)
			.filter(token -> token != null && !token.isEmpty())
			.toList();

		String title = notificationRequest.title();
		String body = notificationRequest.body();

		if (tokens.size() == 1) {
			fcmService.sendMessage(tokens.get(0), title, body);
		} else {
			fcmService.sendMulticastMessages(tokens, title, body);
		}
	}

	private void saveNotification(NotificationRequest notificationRequest) {
		Notification notification = notificationRequest.toNotification();
		notificationRepository.save(notification);
	}
}
