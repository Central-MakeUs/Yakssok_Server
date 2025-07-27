package server.yakssok.domain.notification.application.service;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.repository.NotificationRepository;
import server.yakssok.domain.notification.presentation.dto.response.NotificationResponse;
import server.yakssok.domain.notification.presentation.dto.request.MedicationNotificationRequest;
import server.yakssok.domain.notification.presentation.dto.request.NotificationRequest;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.entity.UserDevice;
import server.yakssok.domain.user.domain.repository.UserDeviceRepository;
import server.yakssok.domain.user.domain.repository.UserRepository;
import server.yakssok.global.infra.fcm.FcmService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final FcmService fcmService;
	private final UserDeviceRepository userDeviceRepository;
	private final UserRepository userRepository;

	@Transactional
	public void sendNotification(NotificationRequest notificationRequest) {
		Long userId = notificationRequest.receiverId();
		List<UserDevice> devices = userDeviceRepository.findByUserIdAndAlertOnTrue(userId);
		if (devices.isEmpty()) return;

		String title = notificationRequest.title();
		String body = notificationRequest.body();
		sendNotifications(devices, title, body, notificationRequest);
	}

	private void sendNotifications(List<UserDevice> devices, String title, String body, NotificationRequest request) {
		if (devices.size() == 1) {
			sendToSingleDevice(devices.get(0), title, body, request);
		} else {
			sendToMultipleDevices(devices, title, body, request);
		}
	}

	private void sendToSingleDevice(UserDevice device, String title, String body, NotificationRequest request) {
		String token = device.getFcmToken();
		if (token == null || token.isEmpty()) return;

		try {
			fcmService.sendMessage(token, title, body);
			saveNotification(request, true);
		} catch (FirebaseMessagingException e) {
			handleInvalidToken(e, device);
		}
	}

	private void sendToMultipleDevices(List<UserDevice> devices, String title, String body, NotificationRequest request) {
		List<String> tokens = devices.stream()
			.map(UserDevice::getFcmToken)
			.filter(t -> t != null && !t.isEmpty())
			.toList();

		if (tokens.isEmpty()) return;

		try {
			BatchResponse resp = fcmService.sendMulticastMessages(tokens, title, body);
			handleMulticastFailures(devices, resp);

			if (resp.getFailureCount() == 0) {
				saveNotification(request, true);
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

	private void saveNotification(NotificationRequest request, boolean isSuccess) {
		Notification notification = request.toNotification(isSuccess);
		notificationRepository.save(notification);
	}

	//TODO : 위 코드랑 분리 필요
	@Transactional
	public void createNotification(Long userId, MedicationNotificationRequest createNotificationRequest) {
		Notification notification = createNotificationRequest.toNotification(userId, true);
		notificationRepository.save(notification);
	}

	@Transactional(readOnly = true)
	public List<NotificationResponse> findMyNotifications(Long userId) {
		List<Notification> notifications = notificationRepository.findMyNotifications(userId);
		Set<Long> userIds = notifications.stream()
			.flatMap(n -> Stream.of(n.getSenderId(), n.getReceiverId()))
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
			.collect(Collectors.toMap(User::getId, Function.identity()));

		return notifications.stream()
			.map(notification -> {
				User receiver = userMap.get(notification.getReceiverId());

				if (notification.isSystemNotification()) {
					return NotificationResponse.of(notification, receiver);
				} else {
					User sender = userMap.get(notification.getSenderId());
					boolean isSentByMe = notification.isSentBy(userId);
					return NotificationResponse.of(notification, receiver, sender, isSentByMe);
				}

			})
			.toList();
	}
}
