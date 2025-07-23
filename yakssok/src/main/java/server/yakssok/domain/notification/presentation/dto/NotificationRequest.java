package server.yakssok.domain.notification.presentation.dto;

import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.entity.NotificationType;

public record NotificationRequest(
	Long senderId,
	String fcmToken,
	Long receiverId,
	String title,
	String body,
	NotificationType type
) {
	public Notification toNotification() {
		return Notification.createNotification(
			senderId,
			receiverId,
			title,
			body,
			type
			);

	}
}
