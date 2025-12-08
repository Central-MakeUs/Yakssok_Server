package server.yakssok.domain.medication_schedule.batch.job;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleAlarmDto;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.notification.application.service.PushService;
import server.yakssok.domain.notification.presentation.dto.NotificationDTO;

@Component
@RequiredArgsConstructor
public class MedicationAlarmJob {
	private final PushService pushService;
	private final MedicationScheduleRepository medicationScheduleRepository;

	@Transactional
	public void sendMedicationAlarms() {
		LocalDateTime now = LocalDateTime.now();
		List<MedicationScheduleAlarmDto> scheduledMedications
			= medicationScheduleRepository.findSchedules(now);
		for (MedicationScheduleAlarmDto schedule : scheduledMedications) {
			NotificationDTO notificationDTO = NotificationDTO.fromMedicationSchedule(schedule);
			pushService.sendNotification(notificationDTO);
		}
	}
}
