package server.yakssok.domain.medication_schedule.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.application.service.PushService;
import server.yakssok.domain.notification.presentation.dto.request.NotificationRequest;

@Component
@RequiredArgsConstructor
public class NotTakenConsumer {

	private final PushService pushService;

	@RabbitListener(queues = "not-taken-queue")
	public void receiveNotTaken(NotificationRequest notificationRequest) {
		pushService.sendNotification(notificationRequest);
	}
}