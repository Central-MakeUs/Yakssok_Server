package server.yakssok.domain.medication.presentation.dto.response;

import java.time.LocalTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import server.yakssok.domain.medication.domain.entity.Medication;

@Schema(description = "복약 정보")
public record MedicationCardResponse(
	@Schema(description = "복약 id", example = "1")
	Long medicationId,

	@Schema(description = "약 종류", example = "CHRONIC")
	String medicationType,

	@Schema(description = "약 이름", example = "타이레놀")
	String medicineName,

	@Schema(description = "복약 상태", example = "TAKING")
	String medicationStatus,

	@Schema(description = "복약 요일 리스트", example = "[\"MONDAY\", \"WEDNESDAY\"]")
	List<String> intakeDays,

	@Schema(description = "하루 복약 횟수", example = "3")
	int intakeCount,

	@Schema(description = "복약 시간 리스트 (HH:mm)", example = "[\"08:00:00\", \"13:00:00\"]")
	List<LocalTime> intakeTimes

) {
	public static MedicationCardResponse from(Medication medication) {
		return new MedicationCardResponse(
			medication.getId(),
			medication.getMedicationType().name(),
			medication.getMedicineName(),
			medication.getMedicationStatus().name(),
			medication.getIntakeDays().stream()
				.map(day -> day.getDayOfWeek().name())
				.toList(),
			medication.getIntakeCount(),
			medication.getIntakeTimes().stream()
				.map(time -> time.getTime())
				.toList()
		);
	}
}
