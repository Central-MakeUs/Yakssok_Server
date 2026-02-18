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
		LocalDate end
	) {
		return findSchedules(userId, start, end);
	}

	private List<MedicationScheduleResponse> findSchedules(
		Long userId,
		LocalDate start,
		LocalDate end
	) {
		return medicationScheduleRepository.findUserSchedulesInRange(userId, start, end).stream()
			.map(MedicationScheduleResponse::from)
			.toList();
	}

	public List<MedicationScheduleDto> fetchTodayMissedMedications(List<Long> followingIds, LocalDateTime delayBoundaryTime) {
		return medicationScheduleRepository.findTodayRemainingMedications(followingIds, delayBoundaryTime);
	}

	public List<MedicationScheduleDto> findTodayAllTakenSchedules(List<Long> followingIds, LocalDate today) {
		return medicationScheduleRepository.findTodayAllTakenSchedules(followingIds, today);
	}
}
