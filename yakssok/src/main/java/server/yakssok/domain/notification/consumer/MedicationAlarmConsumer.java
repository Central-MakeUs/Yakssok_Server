package server.yakssok.domain.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.application.service.PushService;
import server.yakssok.domain.notification.presentation.dto.request.NotificationRequest;
import server.yakssok.global.infra.rabbitmq.MedicationQueueProperties;

@Component
@RequiredArgsConstructor
public class MedicationAlarmConsumer {
	private final PushService pushService;
	private final MedicationQueueProperties medicationQueueProperties;

	@RabbitListener(queues = "${rabbitmq.medication.queue}")
	public void receiveMedicationAlarm(NotificationRequest notificationRequest) {
		pushService.sendData(notificationRequest);
	}
}
