package server.yakssok.domain.medication_schedule.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TodayMedicationScheduleResponse(
	@Schema(description = "복용일 (yyyy-MM-dd)", example = "2025-07-14")
	String date,
	@Schema(description = "스케줄 id", example = "1")
	Long scheduleId,
	@Schema(description = "약 종류", example = "CHRONIC")
	String medicationType,
	@Schema(description = "약 이름", example = "타이레놀")
	String medicationName,
	@Schema(description = "복용 시간", example = "14:00")
	String intakeTime,
	@Schema(description = "복용 여부", example = "true")
	boolean isTaken
) {
	public static TodayMedicationScheduleResponse from(
		String date,
		Long scheduleId,
		String medicationType,
		String medicationName,
		String intakeTime,
		boolean isTaken
	) {
		return new TodayMedicationScheduleResponse(date, scheduleId, medicationType, medicationName, intakeTime, isTaken);
	}
}
