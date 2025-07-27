package server.yakssok.domain.notification.presentation.dto.response;

import java.time.LocalDateTime;

import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.user.domain.entity.User;

public record NotificationResponse(
	Long notificationId,
	String notificationType,
	String senderNickName,
	String senderProfileUrl,
	String receiverNickName,
	String receiverProfileUrl,
	String content,
	LocalDateTime createdAt,
	boolean isSentByMe
) {
	public static final String SYSTEM_SENDER_NAME = "약쏙";

	public static NotificationResponse ofFeedbackNotification(Notification notification, User sender, User receiver, boolean isSentByMe) {
		String senderNickName = sender.getNickName();
		String senderProfileUrl = sender.getProfileImageUrl();
		String receiverNickName = receiver.getNickName();
		String receiverProfileUrl = receiver.getProfileImageUrl();
		String body = notification.getBody();

		return new NotificationResponse(
			notification.getId(),
			notification.getType().name(),
			senderNickName,
			senderProfileUrl,
			receiverNickName,
			receiverProfileUrl,
			body,
			notification.getCreatedAt(),
			isSentByMe
		);
	}

	public static NotificationResponse ofSystemNotification(Notification notification, User receiver) {
		String receiverNickName = receiver.getNickName();
		String receiverProfileUrl = receiver.getProfileImageUrl();
		String title = notification.getTitle();
		String body = notification.getBody();
		return new NotificationResponse(
			notification.getId(),
			notification.getType().name(),
			SYSTEM_SENDER_NAME,
			null,
			receiverNickName,
			receiverProfileUrl,
			(title + " " + body).trim(),
			notification.getCreatedAt(),
			false
		);
	}
}
