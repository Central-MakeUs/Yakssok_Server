package server.yakssok.domain.medication.presentation.dto.request;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeDay;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeTime;
import server.yakssok.domain.medication.domain.entity.MedicationType;
import server.yakssok.domain.medication.domain.entity.SoundType;

@Schema(description = "약 복용 등록 요청")
public record CreateMedicationRequest(
	@Schema(description = "약 이름", example = "타이레놀")
	@NotNull
	String name,

	@Schema(description = "약 종류", example = "CHRONIC")
	@NotNull
	String medicineType,

	@Schema(description = "복용 시작일 (yyyy-MM-dd)", example = "2025-07-06")
	@NotNull
	@FutureOrPresent(message = "복용 시작일은 오늘 또는 이후여야 합니다.")
	LocalDate startDate,

	@Schema(description = "복용 종료일 (yyyy-MM-dd)", example = "2025-07-13")
	LocalDate endDate,

	@Schema(
		description = "복용 요일",
		example = "[\"MONDAY\", \"TUESDAY\", \"WEDNESDAY\"]",
		allowableValues = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"}
	)
	@NotEmpty
	List<DayOfWeek> intakeDays,

	@Schema(description = "하루 복용 횟수", example = "2")
	@NotNull
	Integer intakeCount,

	@Schema(description = "알람 종류", example = "FEEL_GOOD")
	@NotNull
	String alarmSound,

	@Schema(
		description = "복용 시간",
		example = "[\"08:00:00\", \"13:00:00\"]"
	)
	@NotEmpty
	List<LocalTime> intakeTimes
) {

	public Medication toMedication(Long userId) {
		return Medication.create(
			name,
			startDate,
			endDate,
			SoundType.from(alarmSound),
			MedicationType.from(medicineType),
			userId,
			intakeCount
		);
	}

	public List<MedicationIntakeTime> toMedicationsTimes(Medication medication){
		return intakeTimes.stream()
			.map(intakeTime -> MedicationIntakeTime.create(
				intakeTime, medication
			))
			.toList();
	}

	public List<MedicationIntakeDay> toIntakeDays(Medication medication) {
		return intakeDays.stream()
			.map(dayOfWeek -> MedicationIntakeDay.of(dayOfWeek, medication))
			.toList();
	}
}
