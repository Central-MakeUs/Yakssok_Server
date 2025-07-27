package server.yakssok.domain.notification.presentation.dto.response;

import java.time.LocalDateTime;

import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.user.domain.entity.User;

public record NotificationResponse(
	Long notificationId,
	String senderNickName,
	String senderProfileUrl,
	String receiverNickName,
	String receiverProfileUrl,
	String content,
	LocalDateTime createdAt,
	boolean isSentByMe
) {
	public static NotificationResponse of(Notification notification, User sender, User receiver, boolean isSentByMe) {
		String senderNick = sender.getNickName();
		String senderProfile = sender.getProfileImageUrl();
		String receiverNick = receiver.getNickName();
		String receiverProfile = receiver.getProfileImageUrl();
		String title = notification.getTitle();
		String body = notification.getBody();

		return new NotificationResponse(
			notification.getId(),
			senderNick,
			senderProfile,
			receiverNick,
			receiverProfile,
			(title + " " + body).trim(),
			notification.getCreatedAt(),
			isSentByMe
		);
	}

	public static NotificationResponse of(Notification notification, User receiver) {
		String title = notification.getTitle();
		String body = notification.getBody();
		return new NotificationResponse(
			notification.getId(),
			"약쏙",
			null,
			receiver.getNickName(),
			receiver.getProfileImageUrl(),
			(title + " " + body).trim(),
			notification.getCreatedAt(),
			false
		);
	}
}
