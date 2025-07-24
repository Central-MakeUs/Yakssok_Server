package server.yakssok.domain.notification.presentation.dto;

import server.yakssok.domain.feeback.domain.entity.Feedback;
import server.yakssok.domain.notification.application.service.NotificationTitleUtils;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.entity.NotificationType;

public record NotificationRequest(
	Long senderId,
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

	public static NotificationRequest fromFeedback(Long senderId, String senderName, Long receiverId, Feedback feedback) {
		return new NotificationRequest(
			senderId,
			receiverId,
			NotificationTitleUtils.createFeedbackTitle(feedback.getFeedbackType(), senderName),
			feedback.getMessage(),
			NotificationType.FEEDBACK
		);
	}
}
