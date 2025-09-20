package server.yakssok.domain.medication_schedule.batch.job;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.application.service.MedicationScheduleService;

@Component
@RequiredArgsConstructor
public class MedicationScheduleJob {

	private final MedicationScheduleService medicationScheduleService;
	public void runToday() {
		medicationScheduleService.generateTodaySchedules();
	}
}
