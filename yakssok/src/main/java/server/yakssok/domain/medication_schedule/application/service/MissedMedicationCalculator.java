package server.yakssok.domain.medication_schedule.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;
import server.yakssok.domain.medication_schedule.domain.policy.OverduePolicy;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

@Service
public class MissedMedicationCalculator {

	/** 마지막 잔소리 이후의 미복용 카운트 맵 */
	public Map<Long, Integer> countMissedAfterLastNag(
		List<MedicationSchedule> notTakenSchedules,
		Map<Long, LocalDateTime> lastNagByUser,
		LocalDate today,
		OverduePolicy policy
	) {
		Map<Long, Integer> map = new HashMap<>();
		LocalDateTime startOfDay = today.atStartOfDay();

		for (MedicationSchedule ms : notTakenSchedules) {
			Long uid = ms.getUserId();
			LocalDateTime nagBoundary = lastNagByUser.getOrDefault(uid, startOfDay);
			if (ms.isOverdueAfterNag(nagBoundary, policy)) {
				map.merge(uid, 1, Integer::sum);
			}
		}
		return map;
	}

	/** detail: 마지막 잔소리 이후의 미복용 상세 필터 */
	public List<MedicationScheduleDto> filterOverdueAfterNag(
		List<MedicationScheduleDto> schedules, LocalDateTime nagBoundary, OverduePolicy policy
	) {
		return schedules.stream()
			.filter(dto -> LocalDateTime.of(dto.date(), dto.intakeTime())
				.plusMinutes(policy.graceMinutes())
				.isAfter(nagBoundary))
			.toList();
	}
}
