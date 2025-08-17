package server.yakssok.domain.medication_schedule.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.application.service.MedicationScheduleGenerator;
import server.yakssok.domain.medication_schedule.application.exception.MedicationScheduleException;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;
import server.yakssok.domain.medication_schedule.presentation.dto.response.MedicationScheduleResponse;
import server.yakssok.global.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class MedicationScheduleFinder {

	private final MedicationScheduleRepository medicationScheduleRepository;
	private final MedicationScheduleGenerator medicationScheduleGenerator;

	public MedicationSchedule findScheduleById(Long scheduleId) {
		MedicationSchedule schedule = medicationScheduleRepository.findById(scheduleId)
			.orElseThrow(() -> new MedicationScheduleException(ErrorCode.NOT_FOUND_MEDICATION_SCHEDULE));
		return schedule;
	}

	public List<MedicationScheduleResponse> findSchedulesByDate(Long userId, LocalDate date) {
		return medicationScheduleRepository.findUserSchedulesByDate(userId, date).stream()
			.map(MedicationScheduleResponse::from)
			.toList();
	}

	public List<MedicationScheduleResponse> findSchedulesInRange(
		Long userId,
		LocalDate start,
		LocalDate end,
		LocalDate today
	) {
		if (isEntirelyPast(end, today)) {
			return findPastSchedules(userId, start, end);
		}
		if (isMixedPeriod(start, end, today)) {
			return findMixedSchedules(userId, start, end, today);
		}
		return findFutureSchedules(userId, start, end);
	}

	private boolean isEntirelyPast(LocalDate end, LocalDate today) {
		return !end.isAfter(today);
	}

	private boolean isMixedPeriod(LocalDate start, LocalDate end, LocalDate today) {
		return !start.isAfter(today) && end.isAfter(today);
	}

	private List<MedicationScheduleResponse> findPastSchedules(
		Long userId,
		LocalDate start,
		LocalDate end
	) {
		return medicationScheduleRepository.findUserSchedulesInPastRange(userId, start, end).stream()
			.map(MedicationScheduleResponse::from)
			.toList();
	}

	private List<MedicationScheduleResponse> findMixedSchedules(
		Long userId,
		LocalDate start,
		LocalDate end,
		LocalDate today
	) {
		List<MedicationScheduleResponse> past = medicationScheduleRepository.findUserSchedulesInPastRange(userId, start, today).stream()
			.map(MedicationScheduleResponse::from)
			.toList();

		List<MedicationScheduleResponse> future = medicationScheduleGenerator.generateUserFutureScheduleDtos(userId, today.plusDays(1), end).stream()
			.map(MedicationScheduleResponse::from)
			.toList();

		return Stream.concat(past.stream(), future.stream()).toList();
	}

	private List<MedicationScheduleResponse> findFutureSchedules(
		Long userId,
		LocalDate start,
		LocalDate end
	) {
		return medicationScheduleGenerator.generateUserFutureScheduleDtos(userId, start, end).stream()
			.map(MedicationScheduleResponse::from)
			.toList();
	}

	public List<MedicationSchedule> fetchTodayMissedMedications(List<Long> followingIds, LocalDateTime delayBoundaryTime) {
		return medicationScheduleRepository.findTodayRemainingMedications(followingIds, delayBoundaryTime);
	}

	public List<MedicationSchedule> findTodayAllTakenSchedules(List<Long> followingIds, LocalDate today) {
		return medicationScheduleRepository.findTodayAllTakenSchedules(followingIds, today);
	}
}
