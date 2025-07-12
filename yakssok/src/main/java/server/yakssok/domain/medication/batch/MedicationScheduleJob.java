package server.yakssok.domain.medication.batch;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.application.service.MedicationScheduleService;

@Component
@RequiredArgsConstructor
public class MedicationScheduleJob {

	private final MedicationScheduleService medicationScheduleService;

	public void runToday() {
		medicationScheduleService.generateTodaySchedules();
	}
}
