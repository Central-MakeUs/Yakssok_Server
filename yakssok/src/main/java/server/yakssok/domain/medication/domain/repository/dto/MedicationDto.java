package server.yakssok.domain.medication.domain.repository.dto;

import java.time.LocalTime;

public record MedicationDto(
	Long medicationId,
	String medicineName,
	LocalTime intakeTime,
	Long userId
) {}