package server.yakssok.domain.medication_schedule.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.applcation.service.RelationshipService;
import server.yakssok.domain.medication.application.service.MedicationScheduleGenerator;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleJdbcRepository;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.medication_schedule.presentation.dto.MedicationScheduleGroupResponse;
import server.yakssok.domain.medication_schedule.presentation.dto.MedicationScheduleResponse;

@Service
@RequiredArgsConstructor
public class MedicationScheduleService {

	private final MedicationScheduleJdbcRepository medicationScheduleJdbcRepository;
	private final MedicationScheduleFinder medicationScheduleFinder;
	private final MedicationScheduleGenerator medicationScheduleGenerator;
	private final MedicationScheduleManager medicationScheduleManager;
	private final RelationshipService relationshipService;
	private final MedicationScheduleValidator medicationScheduleValidator;
	private final MedicationScheduleRepository medicationScheduleRepository;

	@Transactional
	public void generateTodaySchedules() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		List<MedicationSchedule> schedules = medicationScheduleGenerator.generateTodaySchedules(currentDateTime);
		medicationScheduleJdbcRepository.batchInsert(schedules);
	}

	@Transactional
	public void takeMedication(Long userId, Long scheduleId) {
		MedicationSchedule schedule = medicationScheduleFinder.findScheduleById(scheduleId);
		medicationScheduleValidator.validateOwnership(userId, schedule);
		schedule.take();
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
		List<MedicationScheduleResponse> sorted = list.stream()
			.sorted(Comparator.comparing(MedicationScheduleResponse::isTaken)
				.thenComparing(MedicationScheduleResponse::intakeTime))
			.collect(Collectors.toList());
		return MedicationScheduleGroupResponse.fromList(sorted);
	}

	public boolean isExistTodaySchedule(Long userId) {
		return medicationScheduleRepository.existsTodayScheduleByUserId(userId);
	}
}
