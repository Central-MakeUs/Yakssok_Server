package server.yakssok.domain.medication_schedule.domain.repository.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record RemainingMedicationDto(
	Long userId,
	LocalDate scheduledDate,
	LocalTime scheduledTime
) {
}
