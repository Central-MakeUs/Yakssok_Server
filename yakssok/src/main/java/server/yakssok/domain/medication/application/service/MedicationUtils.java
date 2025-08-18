package server.yakssok.domain.medication.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class MedicationUtils {
	public static LocalDateTime toEndOfDay(LocalDate endDate) {
		return LocalDateTime.of(endDate, LocalTime.of(23, 59, 59));
	}
}
