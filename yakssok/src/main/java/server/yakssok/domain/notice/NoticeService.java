package server.yakssok.domain.notice;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notice.controller.SendNoticeRequest;
import server.yakssok.domain.notification.presentation.dto.NotificationDTO;
import server.yakssok.global.infra.rabbitmq.properties.NoticeQueueProperties;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
	private final RabbitTemplate rabbitTemplate;
	private final NoticeQueueProperties noticeQueueProperties;

	public void sendNotice(SendNoticeRequest request) {
		pushNotice(request.userId(), request.title(), request.body());
	}

	private void pushNotice(Long receiverId, String title, String body) {
		NotificationDTO notificationDTO = NotificationDTO.fromNotice(receiverId, title, body);
		pushNoticeQueue(notificationDTO);
	}

	private void pushNoticeQueue(NotificationDTO notificationDTO) {
		String noticeExchange = noticeQueueProperties.exchange();
		String noticeRoutingKey = noticeQueueProperties.routingKey();
		rabbitTemplate.convertAndSend(noticeExchange, noticeRoutingKey, notificationDTO);
	}
}
