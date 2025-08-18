package server.yakssok.domain.medication_schedule.domain.policy;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component
public class DefaultOverduePolicy implements OverduePolicy{
	private static final long GRACE_MINUTES = 30L;

	@Override
	public long graceMinutes() {
		return GRACE_MINUTES;
	}

	@Override
	public LocalDateTime delayBoundary(LocalDateTime now) {
		return now.minusMinutes(GRACE_MINUTES);
	}

	@Override
	public boolean isBeforeDailyGraceWindow(LocalDateTime now) {
		return delayBoundary(now).toLocalDate().isBefore(now.toLocalDate());
	}
}
