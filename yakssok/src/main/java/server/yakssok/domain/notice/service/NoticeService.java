package server.yakssok.domain.notice.service;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notice.controller.SendNoticeRequest;
import server.yakssok.domain.notification.presentation.dto.NotificationAllDTO;
import server.yakssok.global.infra.rabbitmq.properties.NoticeQueueProperties;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
	private final RabbitTemplate rabbitTemplate;
	private final NoticeQueueProperties noticeQueueProperties;

	public void sendNoticeToAll(SendNoticeRequest request) {
		pushNotice(request.title(), request.body());
	}

	private void pushNotice(String title, String body) {
		NotificationAllDTO notificationAllDTO = NotificationAllDTO.fromNotice(title, body);
		pushNoticeQueue(notificationAllDTO);
	}

	private void pushNoticeQueue(NotificationAllDTO notificationAllDTO) {
		String noticeExchange = noticeQueueProperties.exchange();
		String noticeRoutingKey = noticeQueueProperties.routingKey();
		rabbitTemplate.convertAndSend(noticeExchange, noticeRoutingKey, notificationAllDTO);
	}
}
