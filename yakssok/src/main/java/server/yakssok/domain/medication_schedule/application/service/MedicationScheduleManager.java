package server.yakssok.domain.medication_schedule.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;

@Component
@RequiredArgsConstructor
public class MedicationScheduleManager {

	private final MedicationScheduleRepository medicationScheduleRepository;

	public void deleteTodayUpcomingSchedules(Long medicationId, LocalDateTime now) {
		medicationScheduleRepository.deleteTodayUpcomingSchedules(
			medicationId,
			now.toLocalDate(),
			now.toLocalTime()
		);
	}

	public void deleteAllByMedicationIds(List<Long> medicationIds) {
		medicationScheduleRepository.deleteAllByMedicationIds(medicationIds);
	}

	public void deleteAllUpcomingSchedules(Long medicationId, LocalDateTime now) {
		medicationScheduleRepository.deleteAllUpcomingSchedules(
			medicationId,
			now.toLocalDate(),
			now.toLocalTime()
		);
	}
}
