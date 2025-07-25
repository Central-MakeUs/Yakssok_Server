package server.yakssok.domain.notification.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.repository.NotificationRepository;
import server.yakssok.domain.notification.presentation.dto.NotificationRequest;
import server.yakssok.domain.user.domain.entity.UserDevice;
import server.yakssok.domain.user.domain.repository.UserDeviceRepository;
import server.yakssok.global.infra.fcm.FcmService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final FcmService fcmService;
	private final UserDeviceRepository userDeviceRepository;

	@Transactional
	public void sendNotification(NotificationRequest notificationRequest) {
		try {
			pushNotification(notificationRequest);
			log.info("Notification sent successfully to userId: {}", notificationRequest.receiverId());
		} catch (FirebaseMessagingException e) {
			//TODO : 토큰 무효화
			log.warn("Failed to send notification: {}", e.getMessage());
		}
	}

	private void pushNotification(NotificationRequest notificationRequest) throws FirebaseMessagingException {
		Long userId = notificationRequest.receiverId();
		List<String> tokens = getUserFcmTokens(userId);
		if (tokens.isEmpty()) {
			return;
		}
		pushMessages(notificationRequest, tokens);
		saveNotification(notificationRequest, true);
	}

	private List<String> getUserFcmTokens(Long userId) {
		List<String> tokens = userDeviceRepository.findByUserIdAndAlertOnTrue(userId)
			.stream()
			.map(UserDevice::getFcmToken)
			.filter(token -> token != null && !token.isEmpty())
			.toList();
		return tokens;
	}

	private void pushMessages(NotificationRequest notificationRequest, List<String> tokens) throws
		FirebaseMessagingException {
		String title = notificationRequest.title();
		String body = notificationRequest.body();
		if (tokens.size() == 1) {
			fcmService.sendMessage(tokens.get(0), title, body);
		} else {
			fcmService.sendMulticastMessages(tokens, title, body);
		}
	}

	private void saveNotification(NotificationRequest notificationRequest, boolean isSuccess) {
		Notification notification = notificationRequest.toNotification(isSuccess);
		notificationRepository.save(notification);
	}
}
