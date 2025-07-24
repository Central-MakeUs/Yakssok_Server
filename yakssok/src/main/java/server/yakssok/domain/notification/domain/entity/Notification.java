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
	private String title;
	private String body;
	private NotificationType type;

	public Notification(Long senderId, Long receiverId, String title, String body, NotificationType type) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.title = title;
		this.body = body;
		this.type = type;
	}

	public static Notification createNotification(Long senderId, Long receiverId, String title, String body, NotificationType type) {
		return new Notification(senderId, receiverId, title, body, type);
	}
}
