package server.yakssok.domain.medication.presentation.dto.validator;

import java.time.LocalDate;

public interface MedicationDateRange {
	LocalDate startDate();
	LocalDate endDate();
}
