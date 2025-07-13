package server.yakssok.domain.medication.presentation.dto.request;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import server.yakssok.domain.medication.domain.entity.AlarmSound;
import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeDay;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeTime;
import server.yakssok.domain.medication.domain.entity.MedicationType;

@Schema(description = "약 복용 등록 요청")
public record CreateMedicationRequest(
	@Schema(description = "약 이름", example = "타이레놀")
	String name,

	@Schema(description = "약 종류", example = "CHRONIC")
	String medicineType,

	@Schema(description = "복용 시작일 (yyyy-MM-dd)", example = "2025-07-06")
	String startDate,

	@Schema(description = "복용 종료일 (yyyy-MM-dd)", example = "2025-07-13")
	String endDate,

	@Schema(
		description = "복용 요일",
		example = "[\"MONDAY\", \"TUESDAY\", \"WEDNESDAY\"]",
		allowableValues = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"}
	)
	List<String> intakeDays,

	@Schema(description = "하루 복용 횟수", example = "1")
	Integer intakeCount,

	@Schema(description = "알람 종류", example = "YAKSSUK")
	String alarmSound,

	@Schema(
		description = "복용 시간",
		example = "[\"8:00\", \"13:00\"]"
	)
	List<String> intakeTimes
) {

	public Medication toMedication(Long userId) {
		return Medication.create(
			name,
			LocalDate.parse(startDate),
			LocalDate.parse(endDate),
			AlarmSound.from(alarmSound),
			MedicationType.from(medicineType),
			userId,
			intakeCount
		);
	}

	public List<MedicationIntakeTime> toMedicationsTimes(Medication medication){
		return intakeTimes.stream()
			.map(intakeTime -> MedicationIntakeTime.create(
				LocalTime.parse(intakeTime), medication
			))
			.toList();
	}

	public List<MedicationIntakeDay> toIntakeDays(Medication medication) {
		return intakeDays.stream()
			.map(String::toUpperCase)
			.map(DayOfWeek::valueOf)
			.map(dayOfWeek -> MedicationIntakeDay.of(dayOfWeek, medication))
			.toList();
	}
}
