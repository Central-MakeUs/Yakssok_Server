package server.yakssok.domain.medication_schedule.batch;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MedicationScheduleScheduler {
	private final MedicationScheduleJob medicationScheduleJob;

	@Scheduled(cron = "0 0 0 * * *")
	public void generateTodaySchedules() {
		medicationScheduleJob.runToday();
	}
}

