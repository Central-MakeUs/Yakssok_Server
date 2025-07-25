package server.yakssok.domain.medication_schedule.batch.job;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleAlarmDto;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.notification.application.service.NotificationService;
import server.yakssok.domain.notification.presentation.dto.NotificationRequest;

@Component
@RequiredArgsConstructor
public class MedicationAlarmJob {
	private final NotificationService notificationService;
	private final MedicationScheduleRepository medicationScheduleRepository;

	public void run() {
		//모든 사용자의 스케줄을 가져온다."복용 예정 시간 + 30분"이 현재보다 과거인 미복용 데이터 조회
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime threshold = now.minusMinutes(30);
		List<MedicationScheduleAlarmDto> notTakenSchedules = medicationScheduleRepository
			.findNotTakenSchedules(threshold);

		notTakenSchedules.stream()
			.forEach(schedule -> {
				notificationService.sendNotification(
					NotificationRequest.fromMedicationSchedule(schedule)
				);
			});
	}
}
