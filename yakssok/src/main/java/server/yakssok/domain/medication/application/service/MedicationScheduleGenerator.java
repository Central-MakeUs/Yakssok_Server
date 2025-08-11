package server.yakssok.domain.medication.application.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeDay;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeTime;
import server.yakssok.domain.medication.domain.repository.MedicationRepository;
import server.yakssok.domain.medication.domain.repository.dto.FutureMedicationSchedulesDto;
import server.yakssok.domain.medication.domain.repository.dto.MedicationDto;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

@Component
@RequiredArgsConstructor
public class MedicationScheduleGenerator {

	private final MedicationRepository medicationRepository;

	public List<MedicationScheduleDto> generateUserFutureScheduleDtos(Long userId, LocalDate start, LocalDate end) {
		return medicationRepository.findFutureMedicationSchedules(userId).stream()
			.flatMap(schedule -> createMedicationScheduleDtos(schedule, start, end))
			.toList();
	}

	private Stream<MedicationScheduleDto> createMedicationScheduleDtos(
		FutureMedicationSchedulesDto schedule, LocalDate start, LocalDate end) {
		Medication medication = schedule.medication();
		MedicationIntakeDay intakeDay = schedule.medicationIntakeDay();
		MedicationIntakeTime intakeTime = schedule.medicationIntakeTime();

		LocalDate actualStart = getActualStart(start, medication);
		LocalDate actualEnd = getActualEnd(end, medication);

		if (actualEnd.isBefore(actualStart)) {
			return Stream.empty();
		}
		return actualStart.datesUntil(actualEnd.plusDays(1))
			.filter(date -> isSameDayOfWeek(date, intakeDay))
			.map(date -> MedicationScheduleDto.forFutureSchedule(date, medication, intakeTime));
	}


	private boolean isSameDayOfWeek(LocalDate date, MedicationIntakeDay intakeDay) {
		return intakeDay.getDayOfWeek() == date.getDayOfWeek();
	}

	private LocalDate getActualStart(LocalDate inputStart, Medication medication) {
		return inputStart.isAfter(medication.getStartDate()) ? inputStart : medication.getStartDate();
	}

	private LocalDate getActualEnd(LocalDate inputEnd, Medication medication) {
		LocalDate endDate = medication.getEndDate();
		return (endDate == null) ? inputEnd : (inputEnd.isBefore(endDate) ? inputEnd : endDate);
	}

	public List<MedicationSchedule> generateAllTodaySchedules(LocalDateTime currentDateTime) {
		List<MedicationDto> medicationDtos = medicationRepository.findMedicationsForScheduleGeneration(currentDateTime, currentDateTime.getDayOfWeek());
		return medicationDtos.stream()
			.map(dto -> MedicationSchedule.create(currentDateTime.toLocalDate(), dto.intakeTime(), dto.medicationId(), dto.userId()))
			.toList();
	}

	public List<MedicationSchedule> generateTodaySchedules(
		Medication medication, List<LocalTime> intakeTimes) {
		return intakeTimes.stream()
				.map(intakeTime -> MedicationSchedule.create(LocalDate.now(), intakeTime, medication.getId(), medication.getUserId()))
				.collect(Collectors.toList());
	}
}