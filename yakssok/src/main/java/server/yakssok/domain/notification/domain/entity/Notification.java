package server.yakssok.domain.notification.domain.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import server.yakssok.domain.BaseEntity;

public class Notification extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long senderId;
	private Long receiverId;
	private String content;
	private String type; //잔소리, 복약 알림, 복약 리마인드 알림, 지인 미복용 고발 알림
}
