package server.yakssok.domain.medication_schedule.domain.policy;

import java.time.LocalDateTime;

public interface OverduePolicy {
	long graceMinutes();
	LocalDateTime delayBoundary(LocalDateTime now);
	boolean isBeforeDailyGraceWindow(LocalDateTime now);
}
