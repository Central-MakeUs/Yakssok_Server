package server.yakssok.domain.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.application.service.PushService;
import server.yakssok.domain.notification.presentation.dto.NotificationDTO;

@Component
@RequiredArgsConstructor
public class MedicationAlarmConsumer {
	private final PushService pushService;

	@RabbitListener(queues = "${rabbitmq.medication.queue}")
	public void receiveMedicationAlarm(NotificationDTO notificationDTO) {
		pushService.sendData(notificationDTO);
	}
}
