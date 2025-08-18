package server.yakssok.global.dummy;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import server.yakssok.domain.medication.presentation.dto.request.CreateMedicationRequest;

public class MedicationDataFactory {
	// 인우 - 혈압약
	public static CreateMedicationRequest inuBloodPressure() {
		return new CreateMedicationRequest(
			"혈압약",
			"CHRONIC",
			LocalDate.of(2025, 7, 1),
			LocalDate.of(2025, 8, 31),
			List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
				DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
			2,
			"FEEL_GOOD",
			List.of(LocalTime.of(8, 0), LocalTime.of(14, 0))
		);
	}
	// 인우 - 비타민D
	public static CreateMedicationRequest inuVitaminD() {
		return new CreateMedicationRequest(
			"비타민D",
			"SUPPLEMENT",
			LocalDate.of(2025, 7, 10),
			null,
			List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
			1,
			"PILL_SHAKE",
			List.of(LocalTime.of(9, 0))
		);
	}
	// 인우 - 감기약
	public static CreateMedicationRequest inuColdMedicine() {
		return new CreateMedicationRequest(
			"감기약",
			"TEMPORARY",
			LocalDate.of(2025, 7, 22),
			LocalDate.of(2025, 8, 5),
			List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
				DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
			3,
			"VIBRATION",
			List.of(LocalTime.of(8, 0), LocalTime.of(14, 0), LocalTime.of(18, 0))
		);
	}
	// 리아 - 콜라겐
	public static CreateMedicationRequest riaCollagen() {
		return new CreateMedicationRequest(
			"콜라겐",
			"BEAUTY",
			LocalDate.of(2025, 7, 22),
			null,
			List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
			1,
			"SCOLD",
			List.of(LocalTime.of(14, 0))
		);
	}
	// 리아 - 유산균
	public static CreateMedicationRequest riaLactobacillus() {
		return new CreateMedicationRequest(
			"유산균",
			"SUPPLEMENT",
			LocalDate.of(2025, 7, 20),
			LocalDate.of(2025, 9, 20),
			List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
				DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
			1,
			"CALL",
			List.of(LocalTime.of(7, 30))
		);
	}
}