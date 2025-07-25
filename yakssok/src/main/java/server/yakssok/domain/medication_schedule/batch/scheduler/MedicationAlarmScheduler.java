package server.yakssok.domain.medication_schedule.batch.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.batch.job.MedicationAlarmJob;

@Component
@RequiredArgsConstructor
public class MedicationAlarmScheduler {
	private final MedicationAlarmJob medicationAlarmJob;

	@Scheduled(cron = "0 * * * * *")
	public void sendNotTakenAlarms() {
		medicationAlarmJob.run();
	}
}
