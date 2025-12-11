package server.yakssok.domain.medication_schedule.batch.job;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.domain.policy.OverduePolicy;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleAlarmDto;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.notification.application.service.PushService;
import server.yakssok.domain.notification.presentation.dto.NotificationDTO;

@Component
@RequiredArgsConstructor
public class MedicationRemindAlarmJob {

	private final PushService pushService;
	private final MedicationScheduleRepository medicationScheduleRepository;
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
			sendNotification(schedule);
		}
	}

	private void sendNotification(MedicationScheduleAlarmDto schedule) {
		NotificationDTO notificationDTO = NotificationDTO.fromMedicationSchedule(schedule);
		pushService.sendNotification(notificationDTO);
	}
}
