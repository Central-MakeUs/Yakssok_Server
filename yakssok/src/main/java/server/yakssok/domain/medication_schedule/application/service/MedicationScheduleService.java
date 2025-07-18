package server.yakssok.domain.medication_schedule.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.applcation.service.RelationshipService;
import server.yakssok.domain.medication.application.service.MedicationScheduleGenerator;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleJdbcRepository;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;
import server.yakssok.domain.medication_schedule.presentation.dto.MedicationScheduleGroupResponse;
import server.yakssok.domain.medication_schedule.presentation.dto.MedicationScheduleResponse;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class MedicationScheduleService {

	private final MedicationScheduleJdbcRepository medicationScheduleJdbcRepository;
	private final MedicationScheduleRepository medicationScheduleRepository;
	private final MedicationScheduleFinder medicationScheduleFinder;
	private final MedicationScheduleGenerator medicationScheduleGenerator;
	private final RelationshipService relationshipService;

	@Transactional
	public void generateTodaySchedules() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		List<MedicationSchedule> schedules = medicationScheduleGenerator.generateTodaySchedules(currentDateTime);
		medicationScheduleJdbcRepository.batchInsert(schedules);
	}

	public MedicationScheduleGroupResponse findTodayMedicationSchedule(Long userId) {
		List<MedicationScheduleDto> schedules = medicationScheduleFinder.findUserSchedulesByDate(userId, LocalDate.now());
		return MedicationScheduleGroupResponse.fromList(convertToResponses(schedules));
	}

	private List<MedicationScheduleResponse> convertToResponses(List<MedicationScheduleDto> schedules) {
		return schedules.stream()
			.map(MedicationScheduleResponse::from)
			.toList();
	}

	//TODO: 유저인지 확인 필요
	@Transactional
	public void takeMedication(Long userId, Long scheduleId) {
		MedicationSchedule schedule = medicationScheduleRepository.findById(scheduleId)
			.orElseThrow(() -> new MedicationScheduleException(ErrorCode.NOT_FOUND_MEDICATION_SCHEDULE));
		schedule.take();
	}

	private MedicationScheduleGroupResponse findRangeMedicationSchedule(Long userId, LocalDate startDate, LocalDate endDate) {
		LocalDate today = LocalDate.now();
		if (startDate.isAfter(endDate)) {
			throw new MedicationScheduleException(ErrorCode.INVALID_INPUT_VALUE);
		}

		List<MedicationScheduleDto> schedules =
			medicationScheduleFinder.findSchedulesInPeriod(userId, startDate, endDate, today);
		return MedicationScheduleGroupResponse.fromList(sortedResponses(schedules));
	}

	private List<MedicationScheduleResponse> sortedResponses(List<MedicationScheduleDto> schedules) {
		return schedules.stream()
			.map(MedicationScheduleResponse::from)
			.sorted(
				Comparator.comparing(MedicationScheduleResponse::isTaken)
					.thenComparing(MedicationScheduleResponse::intakeTime)
			)
			.toList();
	}

	@Transactional(readOnly = true)
	public MedicationScheduleGroupResponse findMyTodayMedicationSchedule(Long userId) {
		return findTodayMedicationSchedule(userId);
	}

	@Transactional(readOnly = true)
	public MedicationScheduleGroupResponse findMyRangeMedicationSchedule(Long userId, LocalDate startDate,
		LocalDate endDate) {
		return findRangeMedicationSchedule(userId, startDate, endDate);
	}

	@Transactional(readOnly = true)
	public MedicationScheduleGroupResponse findFriendTodayMedicationSchedule(Long userId, Long friendId) {
		relationshipService.validateFriendship(userId, friendId);
		return findTodayMedicationSchedule(friendId);
	}

	@Transactional(readOnly = true)
	public MedicationScheduleGroupResponse findFriendRangeMedicationSchedule(Long userId, Long friendId,
		LocalDate startDate, LocalDate endDate) {
		relationshipService.validateFriendship(userId, friendId);
		return findRangeMedicationSchedule(friendId, startDate, endDate);
	}

	public void deleteTodayUpcomingSchedules(Long medicationId, LocalDateTime now) {
		medicationScheduleRepository.deleteTodayUpcomingSchedules(
			medicationId,
			now.toLocalDate(),
			now.toLocalTime()
		);
	}
}
