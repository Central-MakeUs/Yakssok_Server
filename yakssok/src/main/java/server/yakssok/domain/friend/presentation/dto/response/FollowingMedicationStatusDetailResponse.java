package server.yakssok.domain.friend.presentation.dto.response;

import java.time.LocalTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;
@Schema(description = "지인별 미복용 약 상세 응답")
public record FollowingMedicationStatusDetailResponse(
	@Schema(description = "오늘 미복용 약 개수", example = "2")
	int notTakenCount,
	@Schema(description = "지인 닉네임", example = "엄마")
	String nickName,
	@Schema(description = "관계(호칭)", example = "엄마")
	String relationship,
	@Schema(description = "미복용 약 정보 리스트")
	List<MedicationInfo> notTakenMedications
) {
	public record MedicationInfo(
		@Schema(description = "약 종류", example = "CHRONIC")
		String type,
		@Schema(description = "약 이름", example = "타이레놀")
		String name,
		@Schema(description = "복용 시간 (HH:mm:ss)", type = "string", format="HH:mm:ss", example = "08:00:00")
		LocalTime time
	) {}

	public static FollowingMedicationStatusDetailResponse of(
		String nickName,
		String relationship,
		List<MedicationScheduleDto> notTakenMedications
	) {
		List<MedicationInfo> medicationInfos = notTakenMedications.stream()
			.map(dto -> new MedicationInfo(
				dto.medicationType().name(),
				dto.medicationName(),
				dto.intakeTime()
			))
			.toList();

		return new FollowingMedicationStatusDetailResponse(
			medicationInfos.size(),
			nickName,
			relationship,
			medicationInfos
		);
	}
}
