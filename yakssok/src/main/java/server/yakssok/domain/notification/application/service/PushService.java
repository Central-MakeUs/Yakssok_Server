package server.yakssok.domain.notification.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.yakssok.domain.notification.presentation.dto.NotificationAllDTO;
import server.yakssok.domain.notification.presentation.dto.request.TestSendDataRequest;
import server.yakssok.domain.notification.presentation.dto.NotificationDTO;
import server.yakssok.domain.user.domain.entity.UserDevice;
import server.yakssok.domain.user.domain.repository.UserDeviceRepository;
import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.exception.GlobalException;
import server.yakssok.global.infra.fcm.FcmService;

@Service
@Slf4j
@RequiredArgsConstructor
public class PushService {
	private final UserDeviceRepository userDeviceRepository;
	private final FcmService fcmService;
	private final NotificationService notificationService;

	public void sendData(NotificationDTO notificationDTO) {
		notificationService.saveNotification(notificationDTO);
		Long userId = notificationDTO.receiverId();
		List<UserDevice> devices = userDeviceRepository.findByUserIdAndAlertOnTrue(userId);
		if (devices.isEmpty()) return;

		String title = notificationDTO.title();
		String body = notificationDTO.body();
		String soundType = notificationDTO.soundType();
		sendDataToDevice(devices, title, body, soundType);
	}

	private void sendDataToDevice(List<UserDevice> devices, String title, String body, String soundType) {
		for (UserDevice device : devices) {
			String fcmToken = device.getFcmToken();
			if (fcmToken == null || fcmToken.isBlank()) {
				continue;
			}
			try {
				fcmService.sendData(fcmToken, title, body, soundType);
			} catch (FirebaseMessagingException e) {
				handleInvalidToken(e, device);
			}
		}
	}

	@Transactional
	public void sendNotification(NotificationDTO notificationDTO) {
		notificationService.saveNotification(notificationDTO);
		Long userId = notificationDTO.receiverId();
		List<UserDevice> devices = userDeviceRepository.findByUserIdAndAlertOnTrue(userId);
		if (devices.isEmpty()) return;

		String title = notificationDTO.title();
		String body = notificationDTO.body();
		sendNotificationsToDevice(devices, title, body);
	}

	private void sendNotificationsToDevice(List<UserDevice> devices, String title, String body) {
		if (devices.size() == 1) {
			sendToSingleDevice(devices.get(0), title, body);
		} else {
			sendToMultipleDevices(devices, title, body);
		}
	}

	private void sendToSingleDevice(UserDevice device, String title, String body) {
		String token = device.getFcmToken();
		if (token == null || token.isEmpty()) return;

		try {
			fcmService.sendNotification(token, title, body);
		} catch (FirebaseMessagingException e) {
			handleInvalidToken(e, device);
		}
	}

	private void sendToMultipleDevices(List<UserDevice> devices, String title, String body) {
		List<String> tokens = devices.stream()
			.map(UserDevice::getFcmToken)
			.filter(t -> t != null && !t.isEmpty())
			.toList();

		if (tokens.isEmpty()) return;

		try {
			BatchResponse resp = fcmService.sendMulticastNotifications(tokens, title, body);
			handleMulticastFailures(devices, resp);
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

	@Transactional
	public void sendSendData(TestSendDataRequest request) {
		try {
			String fcmToken = request.fcmToken();
			String title = request.title();
			String body = request.body();
			String soundType = request.soundType();
			fcmService.sendData(fcmToken, title, body, soundType);
		} catch (FirebaseMessagingException e) {
			throw new GlobalException(ErrorCode.INVALID_FCM_TOKEN);
		}
	}

	@Transactional
	public void sendAllNotification(NotificationAllDTO notificationAllDTO) {
		notificationService.saveAllNotifications(notificationAllDTO);
		List<UserDevice> devices = userDeviceRepository.findAllByAlertOnTrue();
		String title = notificationAllDTO.title();
		String body = notificationAllDTO.body();
		sendNotificationsToDevice(devices, title, body);
	}
}
