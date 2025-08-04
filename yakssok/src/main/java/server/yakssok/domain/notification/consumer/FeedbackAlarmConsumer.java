package server.yakssok.domain.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.application.service.PushService;
import server.yakssok.domain.notification.presentation.dto.request.NotificationRequest;

@Component
@RequiredArgsConstructor
public class FeedbackAlarmConsumer {

	private final PushService pushService;

	@RabbitListener(queues = "feedback-queue")
	public void receiveFeedback(NotificationRequest notificationRequest) {
		pushService.sendNotification(notificationRequest);
	}
}
