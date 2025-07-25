package server.yakssok.domain.notification.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import server.yakssok.domain.BaseEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long senderId;
	private Long receiverId;
	private Long scheduleId;
	private String title;
	private String body;
	private NotificationType type;
	private boolean isSuccess;

	public Notification(Long senderId, Long receiverId, Long scheduleId, String title, String body,
		NotificationType type, boolean isSuccess) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.scheduleId = scheduleId;
		this.title = title;
		this.body = body;
		this.type = type;
		this.isSuccess = isSuccess;
	}

	public static Notification createNotification(Long senderId, Long receiverId, Long scheduleId, String title, String body, NotificationType type, boolean isSuccess) {
		return new Notification(senderId, receiverId, scheduleId, title, body, type, isSuccess);
	}
}
