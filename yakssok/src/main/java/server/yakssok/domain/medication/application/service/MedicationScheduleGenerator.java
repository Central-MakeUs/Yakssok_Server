package server.yakssok.domain.medication.application.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeDay;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;

@Component
public class MedicationScheduleGenerator {

	public List<MedicationSchedule> generateSchedulesAfter(
		Medication medication,
		List<LocalTime> intakeTimes,
		List<DayOfWeek> intakeDays,
		LocalDateTime cutoff
	) {
		LocalDate effectiveStart = effectiveStart(medication, cutoff.toLocalDate());
		return effectiveStart.datesUntil(medication.getEndDate().plusDays(1))
			.filter(date -> intakeDays.contains(date.getDayOfWeek()))
			.flatMap(date -> timesAfterCutoff(date, intakeTimes, cutoff)
				.map(time -> toSchedule(date, time, medication)))
			.toList();
	}

	public List<MedicationSchedule> generateAllSchedules(
		Medication medication,
		List<LocalTime> intakeTimes
	) {
		List<DayOfWeek> intakeDays = toDayOfWeeks(medication);
		return medication.getStartDate().datesUntil(medication.getEndDate().plusDays(1))
			.filter(date -> intakeDays.contains(date.getDayOfWeek()))
			.flatMap(date -> intakeTimes.stream()
				.map(time -> toSchedule(date, time, medication)))
			.toList();
	}

	private LocalDate effectiveStart(Medication medication, LocalDate today) {
		return medication.getStartDate().isAfter(today) ? medication.getStartDate() : today;
	}

	private Stream<LocalTime> timesAfterCutoff(LocalDate date, List<LocalTime> times, LocalDateTime cutoff) {
		return times.stream().filter(time -> LocalDateTime.of(date, time).isAfter(cutoff));
	}

	private List<DayOfWeek> toDayOfWeeks(Medication medication) {
		return medication.getIntakeDays().stream()
			.map(MedicationIntakeDay::getDayOfWeek)
			.toList();
	}

	private MedicationSchedule toSchedule(LocalDate date, LocalTime time, Medication medication) {
		return MedicationSchedule.create(date, time,
			medication.getId(), medication.getUserId(),
			medication.getMedicineName(), medication.getMedicationType());
	}
}
