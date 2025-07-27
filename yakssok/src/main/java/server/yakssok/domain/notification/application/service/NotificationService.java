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


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.repository.NotificationRepository;
import server.yakssok.domain.notification.presentation.dto.response.NotificationResponse;
import server.yakssok.domain.notification.presentation.dto.request.MedicationNotificationRequest;
import server.yakssok.domain.notification.presentation.dto.request.NotificationRequest;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	void saveNotification(NotificationRequest request) {
		Notification notification = request.toNotification();
		notificationRepository.save(notification);
	}

	@Transactional
	public void createNotification(Long userId, MedicationNotificationRequest createNotificationRequest) {
		Notification notification = createNotificationRequest.toNotification(userId);
		notificationRepository.save(notification);
	}

	@Transactional(readOnly = true)
	public List<NotificationResponse> findMyNotifications(Long userId, Long lastId, int limit) {
		List<Notification> notifications = notificationRepository.findMyNotifications(userId, lastId, limit);
		Set<Long> userIds = getNotificationUserIds(notifications);
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
					return NotificationResponse.of(notification, sender, receiver, isSentByMe);
				}
			})
			.toList();
	}

	private Set<Long> getNotificationUserIds(List<Notification> notifications) {
		Set<Long> userIds = notifications.stream()
			.flatMap(n -> Stream.of(n.getSenderId(), n.getReceiverId()))
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
		return userIds;
	}
}
