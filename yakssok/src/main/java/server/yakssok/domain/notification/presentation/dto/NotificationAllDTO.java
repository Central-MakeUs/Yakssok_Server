package server.yakssok.domain.notification.presentation.dto;


import lombok.AccessLevel;
import lombok.Builder;
import server.yakssok.domain.notification.domain.entity.NotificationType;

@Builder(access = AccessLevel.PRIVATE)
public record NotificationAllDTO(
	String title,
	String body,
	NotificationType type
) {

	public static NotificationAllDTO fromNotice(String title, String body) {
		return NotificationAllDTO.builder()
			.title(title)
			.body(body)
			.type(NotificationType.NOTICE)
			.build();
	}
}
