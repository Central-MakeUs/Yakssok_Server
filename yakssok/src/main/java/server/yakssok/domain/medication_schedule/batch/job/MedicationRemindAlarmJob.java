package server.yakssok.domain.medication_schedule.batch.job;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.domain.policy.OverduePolicy;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleAlarmDto;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.notification.application.service.PushService;
import server.yakssok.domain.notification.presentation.dto.NotificationDTO;
import server.yakssok.global.infra.rabbitmq.properties.MedicationQueueProperties;

@Component
@RequiredArgsConstructor
public class MedicationRemindAlarmJob {

	private final PushService pushService;
	private final MedicationScheduleRepository medicationScheduleRepository;
	private final RabbitTemplate rabbitTemplate;
	private final MedicationQueueProperties medicationQueueProperties;
	private final OverduePolicy overduePolicy;

	@Transactional
	public void sendNotTakenRemindMedicationAlarms() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime delayBoundary = overduePolicy.delayBoundary(now);
		List<MedicationScheduleAlarmDto> notTakenSchedules =
			medicationScheduleRepository.findTodayNotTakenSchedules(delayBoundary);

		Map<Long, List<MedicationScheduleAlarmDto>> byUser =
			notTakenSchedules.stream().collect(Collectors.groupingBy(MedicationScheduleAlarmDto::userId));
		for (Map.Entry<Long, List<MedicationScheduleAlarmDto>> entry : byUser.entrySet()) {
			MedicationScheduleAlarmDto schedule = entry.getValue().get(0);
			sendToMedicationQueue(NotificationDTO.fromNotTakenMedicationSchedule(schedule));
		}
	}

	private void sendToMedicationQueue(NotificationDTO notificationDTO) {
		String exchange = medicationQueueProperties.exchange();
		String routingKey = medicationQueueProperties.routingKey();
		rabbitTemplate.convertAndSend(exchange, routingKey, notificationDTO);
	}
}
