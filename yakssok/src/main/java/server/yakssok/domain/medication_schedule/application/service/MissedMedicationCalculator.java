package server.yakssok.domain.medication_schedule.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import server.yakssok.domain.medication_schedule.domain.policy.OverduePolicy;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

@Service
public class MissedMedicationCalculator {

	public Map<Long, List<MedicationScheduleDto>> missedAfterLastNagSchedules(
		List<MedicationScheduleDto> notTakenSchedules,
		Map<Long, LocalDateTime> lastNagByUser,
		LocalDate today,
		OverduePolicy policy
	) {
		Map<Long, List<MedicationScheduleDto>> map = new HashMap<>();
		LocalDateTime startOfDay = today.atStartOfDay();

		for (MedicationScheduleDto ms : notTakenSchedules) {
			Long userId = ms.userId();
			LocalDateTime nagBoundary = lastNagByUser.getOrDefault(userId, startOfDay);
			LocalDateTime scheduleAt = LocalDateTime.of(ms.date(), ms.intakeTime());
			LocalDateTime effectiveBoundary = policy.delayBoundary(nagBoundary);
			boolean afterBoundary = scheduleAt.isAfter(effectiveBoundary);
			if (afterBoundary) {
				map.computeIfAbsent(userId, k -> new ArrayList<>()).add(ms);
			}
		}
		return map;
	}
}
