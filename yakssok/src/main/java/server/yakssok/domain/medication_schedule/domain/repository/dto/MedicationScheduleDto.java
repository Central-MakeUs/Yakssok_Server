package server.yakssok.domain.medication_schedule.domain.repository.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeTime;
import server.yakssok.domain.medication.domain.entity.MedicationType;

public record MedicationScheduleDto(
	LocalDate date,
	Long scheduleId,
	MedicationType medicationType,
	String medicationName,
	LocalTime intakeTime,
	boolean isTaken
) {
	public static MedicationScheduleDto forFutureSchedule(
		LocalDate date,
		Medication medication,
		MedicationIntakeTime intakeTime
	) {
		return new MedicationScheduleDto(
			date,
			null,
			medication.getMedicationType(),
			medication.getMedicineName(),
			intakeTime.getTime(),
			false
		);
	}
}
