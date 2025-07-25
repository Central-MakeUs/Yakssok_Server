package server.yakssok.domain.notification.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.BatchResponse;
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
		Long userId = notificationRequest.receiverId();
		List<UserDevice> devices = userDeviceRepository.findByUserIdAndAlertOnTrue(userId);
		if (devices.isEmpty()) return;

		String title = notificationRequest.title();
		String body = notificationRequest.body();
		sendNotifications(devices, title, body, notificationRequest);
	}

	private void sendNotifications(List<UserDevice> devices, String title, String body, NotificationRequest req) {
		if (devices.size() == 1) {
			sendToSingleDevice(devices.get(0), title, body, req);
		} else {
			sendToMultipleDevices(devices, title, body, req);
		}
	}

	private void sendToSingleDevice(UserDevice device, String title, String body, NotificationRequest req) {
		String token = device.getFcmToken();
		if (token == null || token.isEmpty()) return;

		try {
			fcmService.sendMessage(token, title, body);
			saveNotification(req, true);
		} catch (FirebaseMessagingException e) {
			handleInvalidToken(e, device);
		}
	}

	private void sendToMultipleDevices(List<UserDevice> devices, String title, String body, NotificationRequest req) {
		List<String> tokens = devices.stream()
			.map(UserDevice::getFcmToken)
			.filter(t -> t != null && !t.isEmpty())
			.toList();

		if (tokens.isEmpty()) return;

		try {
			BatchResponse resp = fcmService.sendMulticastMessages(tokens, title, body);
			handleMulticastFailures(devices, resp);

			if (resp.getFailureCount() == 0) {
				saveNotification(req, true);
			}
		} catch (FirebaseMessagingException e) {
			log.warn("Failed to send multicast notification: {}", e.getMessage());
		}
	}

	private void handleMulticastFailures(List<UserDevice> devices, BatchResponse response) {
		for (int i = 0; i < response.getResponses().size(); i++) {
			if (!response.getResponses().get(i).isSuccessful()) {
				UserDevice failedDevice = devices.get(i);
				handleInvalidToken(response.getResponses().get(i).getException(), failedDevice);
			}
		}
	}

	private void handleInvalidToken(Exception e, UserDevice device) {
		if (e instanceof FirebaseMessagingException) {
			FirebaseMessagingException fme = (FirebaseMessagingException) e;
			if (isInvalidTokenError(fme)) {
				log.warn("Invalid FCM token for deviceId: {}", device.getId());
				device.invalidateFcmToken();
			}
		}
	}

	private boolean isInvalidTokenError(FirebaseMessagingException e) {
		String errorCode = e.getMessagingErrorCode() != null ? e.getMessagingErrorCode().name() : "";
		return "UNREGISTERED".equals(errorCode) || "INVALID_ARGUMENT".equals(errorCode);
	}

	private void saveNotification(NotificationRequest req, boolean isSuccess) {
		Notification notification = req.toNotification(isSuccess);
		notificationRepository.save(notification);
	}
}
