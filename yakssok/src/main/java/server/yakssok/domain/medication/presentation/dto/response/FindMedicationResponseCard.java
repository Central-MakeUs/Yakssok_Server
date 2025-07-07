package server.yakssok.domain.medication.presentation.dto.response;

import java.util.List;

import server.yakssok.domain.medication.domain.entity.Medication;

public record FindMedicationResponseCard(
	String medicineName,
	String medicationType,
	String medicationStatus,
	List<String> intakeDays,
	int intakeCount,
	List<String> intakeTimes
) {
	public static FindMedicationResponseCard of(Medication medication) {
		return null;
	}
}
