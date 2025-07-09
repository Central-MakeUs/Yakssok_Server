package server.yakssok.domain.medication.presentation.dto.response;

import java.util.List;

import server.yakssok.domain.medication.domain.entity.Medication;

public record MedicationCardResponse(
	String medicineName,
	String medicationStatus,
	List<String> intakeDays,
	int intakeCount,
	List<String> intakeTimes
) {
	public static MedicationCardResponse from(Medication medication) {
		return new MedicationCardResponse(
			medication.getMedicineName(),
			medication.getMedicationStatus().name(),
			medication.getIntakeDays().stream()
				.map(day -> day.getDayOfWeek().name())
				.toList(),
			medication.getIntakeCount(),
			medication.getIntakeTimes().stream()
				.map(time -> time.getTime().toString())
				.toList()
		);
	}
}
