package server.yakssok.domain.notification.presentation.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.user.domain.entity.User;

public record NotificationResponse(
	@Schema(description = "알림 ID", example = "1")
	Long notificationId,

	@Schema(description = "알림 타입", example = "FEEDBACK_PRAISE")
	String notificationType,

	@Schema(description = "보낸 사람 닉네임", example = "인우")
	String senderNickName,

	@Schema(description = "보낸 사람 프로필 URL", example = "https://cdn.example.com/profile/abc.jpg")
	String senderProfileUrl,

	@Schema(description = "받는 사람 닉네임", example = "리아")
	String receiverNickName,

	@Schema(description = "받는 사람 프로필 URL", example = "https://cdn.example.com/profile/def.jpg")
	String receiverProfileUrl,

	@Schema(description = "알림 내용", example = "약 먹었어? 대단하다~")
	String content,

	@Schema(description = "알림 생성 시간", example = "2024-07-27T13:22:01.123")
	LocalDateTime createdAt,

	@Schema(description = "내가 보낸 알림 여부", example = "false")
	boolean isSentByMe
) {
	private static final String SYSTEM_SENDER_NAME = "약쏙";
	private static final String DELETED_USER_NAME = "알 수 없음";

	private static String getUserNickName(User user) {
		if (user.isDeleted())
			return DELETED_USER_NAME;
		else
			return user.getNickName();
	}


	public static NotificationResponse ofFeedbackNotification(Notification notification, User sender, User receiver, boolean isSentByMe) {
		String senderNickName = getUserNickName(sender);
		String senderProfileUrl = sender.getProfileImageUrl();
		String receiverNickName = getUserNickName(receiver);
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
		String receiverNickName = getUserNickName(receiver);
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
