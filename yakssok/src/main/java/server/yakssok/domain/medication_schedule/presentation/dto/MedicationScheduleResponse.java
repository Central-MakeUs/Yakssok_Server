package server.yakssok.domain.medication_schedule.presentation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;
@Schema(description = "복용일별 복약 스케줄 응답")
public record MedicationScheduleResponse(
	@Schema(description = "복용일 (yyyy-MM-dd)", example = "2025-07-14")
	LocalDate date,
	@Schema(description = "스케줄 id", example = "1")
	Long scheduleId,
	@Schema(description = "약 종류", example = "CHRONIC")
	String medicationType,
	@Schema(description = "약 이름", example = "타이레놀")
	String medicationName,
	@Schema(description = "복용 시간", type = "string", example = "14:00:00")
	LocalTime intakeTime,
	@Schema(description = "복용 여부", example = "true")
	boolean isTaken
) {
	public static MedicationScheduleResponse from(
		MedicationScheduleDto medicationScheduleDto
	) {
		return new MedicationScheduleResponse(
			medicationScheduleDto.date(),
			medicationScheduleDto.scheduleId(),
			medicationScheduleDto.medicationType().name(),
			medicationScheduleDto.medicationName(),
			medicationScheduleDto.intakeTime(),
			medicationScheduleDto.isTaken()
		);
	}
}
