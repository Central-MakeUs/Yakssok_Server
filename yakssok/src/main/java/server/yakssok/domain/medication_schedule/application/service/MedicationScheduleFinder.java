package server.yakssok.domain.medication_schedule.application.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.application.service.MedicationScheduleGenerator;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

@Component
@RequiredArgsConstructor
public class MedicationScheduleFinder {

	private final MedicationScheduleRepository medicationScheduleRepository;
	private final MedicationScheduleGenerator medicationScheduleGenerator;

	public List<MedicationScheduleDto> findUserSchedulesByDate(Long userId, LocalDate date) {
		return medicationScheduleRepository.findUserSchedulesByDate(userId, date);
	}

	public List<MedicationScheduleDto> findSchedulesInPeriod(Long userId, LocalDate start, LocalDate end, LocalDate today) {
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

	private List<MedicationScheduleDto> findPastSchedules(Long userId, LocalDate start, LocalDate end) {
		return medicationScheduleRepository.findUserSchedulesInPastRange(userId, start, end);
	}

	private List<MedicationScheduleDto> findMixedSchedules(Long userId, LocalDate start, LocalDate end, LocalDate today) {
		List<MedicationScheduleDto> schedules = new ArrayList<>(
			medicationScheduleRepository.findUserSchedulesInPastRange(userId, start, today));
		schedules.addAll(medicationScheduleGenerator.generateFutureScheduleDtos(userId, today.plusDays(1), end));
		return schedules;
	}

	private List<MedicationScheduleDto> findFutureSchedules(Long userId, LocalDate start, LocalDate end) {
		return medicationScheduleGenerator.generateFutureScheduleDtos(userId, start, end);
	}
}
