package server.yakssok.domain.notification.presentation.dto;

import server.yakssok.domain.feeback.domain.entity.Feedback;
import server.yakssok.domain.notification.application.service.NotificationTitleUtils;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.entity.NotificationType;
import server.yakssok.domain.user.domain.entity.User;

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

	public static NotificationRequest fromFeedback(User sender, User receiver, Feedback feedback, NotificationType notificationType) {
		return new NotificationRequest(
			sender.getId(),
			receiver.getId(),
			NotificationTitleUtils.createFeedbackTitle(feedback.getFeedbackType(), sender.getNickName()),
			feedback.getMessage(),
			notificationType
		);
	}
}
