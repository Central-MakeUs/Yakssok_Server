package server.yakssok.domain.medication.presentation.dto.response;

import java.util.List;


public record MedicationCardResponse(
	String medicineName,
	String medicationStatus,
	List<String> intakeDays,
	int intakeCount,
	List<String> intakeTimes
) {
}
