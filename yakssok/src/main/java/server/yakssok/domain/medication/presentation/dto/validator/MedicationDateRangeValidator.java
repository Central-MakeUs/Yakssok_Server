package server.yakssok.domain.medication.presentation.dto.validator;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MedicationDateRangeValidator implements ConstraintValidator<ValidMedicationDateRange, MedicationDateRange> {

	@Override
	public boolean isValid(MedicationDateRange request, ConstraintValidatorContext context) {
		if (request.startDate() == null || request.endDate() == null) {
			return true; // null 체크는 @NotNull이 담당
		}
		LocalDate maxDate = LocalDate.of(LocalDate.now().getYear() + 1, 12, 31);
		if (request.startDate().isAfter(maxDate) || request.endDate().isAfter(maxDate)) {
			return false;
		}
		return !request.startDate().isAfter(request.endDate());
	}
}