package server.yakssok.domain.medication_schedule.batch.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.batch.job.MedicationRemindAlarmJob;

@Component
@RequiredArgsConstructor
public class MedicationRemindAlarmScheduler {
	private final MedicationRemindAlarmJob medicationRemindAlarmJob;

	@Scheduled(cron = "0 0 21 * * *")
	public void sendNotTakenRemindMedicationAlarms() {
		medicationRemindAlarmJob.sendNotTakenRemindMedicationAlarms();
	}
}
