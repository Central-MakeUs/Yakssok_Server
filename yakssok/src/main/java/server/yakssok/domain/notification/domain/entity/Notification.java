package server.yakssok.domain.notification.domain.entity;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.yakssok.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long senderId;
	private Long receiverId;
	private Long scheduleId;
	private String title;
	private String body;
	@Enumerated(EnumType.STRING)
	private NotificationType type;

	@Builder
	private Notification(Long senderId, Long receiverId, Long scheduleId, String title, String body,
		NotificationType type) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.scheduleId = scheduleId;
		this.title = title;
		this.body = body;
		this.type = type;
	}

	public static Notification createNotification(Long senderId, Long receiverId, Long scheduleId, String title, String body, NotificationType type) {
		return Notification.builder()
			.senderId(senderId)
			.receiverId(receiverId)
			.scheduleId(scheduleId)
			.title(title)
			.body(body)
			.type(type)
			.build();
	}

	public static Notification createNoticieNotification(Long receiverId, String title, String body, NotificationType type) {
		return Notification.builder()
			.receiverId(receiverId)
			.title(title)
			.body(body)
			.type(type)
			.build();
	}

	public boolean isSentBy(Long userId) {
		return Objects.equals(this.senderId, userId);
	}

	public boolean isSystemNotification() {
		return this.senderId == null;
	}
}
