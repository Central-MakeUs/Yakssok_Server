package server.yakssok.domain.medication_schedule.application.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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


	public List<MedicationScheduleDto> generateFutureScheduleDtos(Long userId, LocalDate start, LocalDate end) {
		return medicationRepository.findFutureMedicationSchedules(userId).stream()
			.flatMap(schedule -> createMedicationScheduleDtos(schedule, start, end))
			.toList();
	}

	private Stream<MedicationScheduleDto> createMedicationScheduleDtos(FutureMedicationSchedulesDto schedule,
		LocalDate start, LocalDate end) {

		Medication medication = schedule.medication();
		MedicationIntakeDay intakeDay = schedule.medicationIntakeDay();
		MedicationIntakeTime intakeTime = schedule.medicationIntakeTime();

		LocalDate actualStart = start.isAfter(medication.getStartDate()) ? start : medication.getStartDate();
		LocalDate actualEnd = (medication.getEndDate() == null) ? end
			: (end.isBefore(medication.getEndDate()) ? end : medication.getEndDate());

		if (actualEnd.isBefore(actualStart)) {
			return Stream.empty();
		}

		return actualStart.datesUntil(actualEnd.plusDays(1))
			.filter(date -> intakeDay.getDayOfWeek() == date.getDayOfWeek())
			.map(date -> new MedicationScheduleDto(
				date,
				null,
				medication.getMedicationType(),
				medication.getMedicineName(),
				intakeTime.getTime(),
				false
			));
	}

	public List<MedicationSchedule> generateTodaySchedules(LocalDate today) {
		List<MedicationDto> medicationDtos = medicationRepository.findMedicationsByDate(today, today.getDayOfWeek());

		return medicationDtos.stream()
			.map(dto -> MedicationSchedule.create(today, dto.intakeTime(), dto.medicationId()))
			.toList();
	}
}