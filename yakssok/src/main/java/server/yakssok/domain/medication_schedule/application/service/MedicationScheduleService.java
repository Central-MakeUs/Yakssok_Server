package server.yakssok.domain.medication_schedule.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.application.service.RelationshipService;
import server.yakssok.domain.medication.application.service.MedicationScheduleGenerator;
import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleJdbcRepository;
import server.yakssok.domain.medication_schedule.presentation.dto.response.DailyMedicationScheduleGroupResponse;
import server.yakssok.domain.medication_schedule.presentation.dto.response.MedicationScheduleGroupResponse;
import server.yakssok.domain.medication_schedule.presentation.dto.response.MedicationScheduleResponse;

@Service
@RequiredArgsConstructor
public class MedicationScheduleService {

	private final MedicationScheduleJdbcRepository medicationScheduleJdbcRepository;
	private final MedicationScheduleFinder medicationScheduleFinder;
	private final MedicationScheduleGenerator medicationScheduleGenerator;
	private final MedicationScheduleManager medicationScheduleManager;
	private final RelationshipService relationshipService;
	private final MedicationScheduleValidator medicationScheduleValidator;

	@Transactional
	public void generateTodaySchedules() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		List<MedicationSchedule> schedules = medicationScheduleGenerator.generateAllTodaySchedules(currentDateTime);
		medicationScheduleJdbcRepository.batchInsert(schedules, 100);
	}

	@Transactional
	public void switchTakeMedication(Long userId, Long scheduleId) {
		MedicationSchedule schedule = medicationScheduleFinder.findScheduleById(scheduleId);
		medicationScheduleValidator.validateOwnership(userId, schedule);
		medicationScheduleValidator.validateTodaySchedule(schedule);
		schedule.switchTake();
	}

	public void deleteTodayUpcomingSchedules(Long medicationId, LocalDateTime now) {
		medicationScheduleManager.deleteTodayUpcomingSchedules(medicationId, now);
	}

	@Transactional(readOnly = true)
	public MedicationScheduleGroupResponse getMyTodaySchedules(Long userId) {
		return groupAndSort(medicationScheduleFinder.findSchedulesByDate(userId, LocalDate.now()));
	}

	@Transactional(readOnly = true)
	public MedicationScheduleGroupResponse getMyRangeSchedules(Long userId, LocalDate start, LocalDate end) {
		return groupAndSort(medicationScheduleFinder.findSchedulesInRange(userId, start, end, LocalDate.now()));
	}

	@Transactional(readOnly = true)
	public MedicationScheduleGroupResponse getFriendTodaySchedules(Long userId, Long friendId) {
		relationshipService.validateFriendship(userId, friendId);
		return getMyTodaySchedules(friendId);
	}

	@Transactional(readOnly = true)
	public MedicationScheduleGroupResponse getFriendRangeSchedules(Long userId, Long friendId,
		LocalDate start, LocalDate end) {
		relationshipService.validateFriendship(userId, friendId);
		return getMyRangeSchedules(friendId, start, end);
	}

	private MedicationScheduleGroupResponse groupAndSort(List<MedicationScheduleResponse> list) {
		Map<LocalDate, List<MedicationScheduleResponse>> grouped = list.stream()
			.collect(Collectors.groupingBy(MedicationScheduleResponse::date));

		List<DailyMedicationScheduleGroupResponse> dailyGroups = grouped.entrySet().stream()
			.map(entry -> {
				LocalDate date = entry.getKey();
				List<MedicationScheduleResponse> schedules = entry.getValue().stream()
					.sorted(Comparator.comparing(MedicationScheduleResponse::isTaken)
						.thenComparing(MedicationScheduleResponse::intakeTime))
					.collect(Collectors.toList());

				return DailyMedicationScheduleGroupResponse.of(date, schedules);
			})
			.sorted(Comparator.comparing(DailyMedicationScheduleGroupResponse::date))
			.collect(Collectors.toList());

		return MedicationScheduleGroupResponse.fromList(dailyGroups);
	}

	public void createTodaySchedules(
		Medication medication, List<LocalTime> intakeTimes) {
		List<MedicationSchedule> schedules = medicationScheduleGenerator.generateTodaySchedules(
			medication, intakeTimes);
		medicationScheduleJdbcRepository.batchInsert(schedules, 100);
	}

	public void deleteAllByMedicationIds(List<Long> medicationIds) {
		medicationScheduleManager.deleteAllByMedicationIds(medicationIds);
	}
}
