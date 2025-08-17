package server.yakssok.domain.medication_schedule.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;
import server.yakssok.domain.medication_schedule.domain.policy.OverduePolicy;

@Service
public class MissedMedicationCalculator {

	public Map<Long, List<MedicationSchedule>> missedAfterLastNagSchedules(
		List<MedicationSchedule> notTakenSchedules,
		Map<Long, LocalDateTime> lastNagByUser,
		LocalDate today,
		OverduePolicy policy
	) {
		Map<Long, List<MedicationSchedule>> map = new HashMap<>();
		LocalDateTime startOfDay = today.atStartOfDay();

		for (MedicationSchedule ms : notTakenSchedules) {
			Long userId = ms.getUserId();
			LocalDateTime nagBoundary = lastNagByUser.getOrDefault(userId, startOfDay);
			if (ms.isOverdueAfterNag(nagBoundary, policy)) {
				map.computeIfAbsent(userId, k -> new ArrayList<>()).add(ms);
			}
		}
		return map;
	}
}
