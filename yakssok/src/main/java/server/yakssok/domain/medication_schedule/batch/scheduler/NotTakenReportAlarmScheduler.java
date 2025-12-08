package server.yakssok.domain.medication_schedule.batch.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.batch.job.NotTakenReportAlarmJob;

@Component
@RequiredArgsConstructor
public class NotTakenReportAlarmScheduler {

	private final NotTakenReportAlarmJob notTakenReportAlarmJob;

	@Scheduled(cron = "0 * * * * *")
	public void run() {
		notTakenReportAlarmJob.sendNotTakenReportMedicationAlarms();
	}
}
