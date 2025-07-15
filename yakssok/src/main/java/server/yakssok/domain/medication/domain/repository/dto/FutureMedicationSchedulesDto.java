package server.yakssok.domain.medication.domain.repository.dto;



import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeDay;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeTime;

public record FutureMedicationSchedulesDto(
	Medication medication,
	MedicationIntakeDay medicationIntakeDay,
	MedicationIntakeTime medicationIntakeTime
) {
}
