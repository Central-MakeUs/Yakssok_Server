package server.yakssok.domain.medication.presentation.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

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
		example = "[\"MON\", \"WED\", \"FRI\"]",
		allowableValues = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"}
	)
	List<String> intakeDays,

	@Schema(description = "하루 복용 횟수", example = "2")
	Integer repeatCount,

	@Schema(description = "복용 시간 목록")
	List<IntakeTime> intakeTimes
) {

	@Schema(description = "복용 시간 정보")
	public record IntakeTime(
		@Schema(description = "복용 시간 (HH:mm 형식)", example = "08:00")
		String time
	) {}
}
