package server.yakssok.domain.friend.presentation.dto.response;

import java.time.LocalTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import server.yakssok.domain.feedback.domain.entity.FeedbackType;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;
import server.yakssok.domain.user.domain.entity.User;

public record FollowingMedicationStatusResponse(
	@Schema(description = "지인(팔로잉) 유저의 고유 ID", example = "2")
	Long userId,
	@Schema(description = "지인의 닉네임", example = "수지")
	String nickName,
	@Schema(description = "나와의 관계명", example = "언니")
	String relationName,
	@Schema(description = "프로필 이미지 URL", example = "https://example.com/suzi.jpg")
	String profileImageUrl,

	@Schema(description = "피드백 타입", example = "NAG", allowableValues = "NAG, PRAISE")
	String feedbackType,
	@Schema(description = "잔소리 대상의 안먹은 약 / 칭찬 대상의 먹은 약 개수", example = "1")
	Integer medicationCount,

	@Schema(description = "잔소리 대상의 안먹은 약 / 칭찬 대상의 먹은 약 상세 정보")
	List<MedicationInfo> medicationDetails
) {

	public record MedicationInfo(
		@Schema(description = "약 종류", example = "CHRONIC")
		String type,
		@Schema(description = "약 이름", example = "타이레놀")
		String name,
		@Schema(description = "복용 시간 (HH:mm:ss)", type = "string", format="HH:mm:ss", example = "08:00:00")
		LocalTime time
	) { }

	/* ===== 공통 생성기 ===== */
	private static FollowingMedicationStatusResponse ofCommon(
		Friend friend,
		FeedbackType type,
		List<MedicationInfo> details
	) {
		User following = friend.getFollowing();
		return new FollowingMedicationStatusResponse(
			following.getId(),
			following.getNickName(),
			friend.getRelationName(),
			following.getProfileImageUrl(),
			type.name(),
			details.size(),
			details
		);
	}

	/* ===== NAG (미복용) ===== */
	public static FollowingMedicationStatusResponse ofNag(
		Friend friend,
		List<MedicationScheduleDto> notTakenDtos
	) {
		return ofCommon(friend, FeedbackType.NAG, toMedicationInfos(notTakenDtos));
	}

	/* ===== PRAISE (복용) ===== */
	public static FollowingMedicationStatusResponse ofPraise(
		Friend friend,
		List<MedicationScheduleDto> takenDtos
	) {
		return ofCommon(friend, FeedbackType.PRAISE, toMedicationInfos(takenDtos));
	}

	private static List<MedicationInfo> toMedicationInfos(List<MedicationScheduleDto> dtos) {
		if (dtos == null || dtos.isEmpty()) return List.of();
		return dtos.stream()
			.map(FollowingMedicationStatusResponse::toMedicationInfo)
			.toList();
	}

	private static MedicationInfo toMedicationInfo(MedicationScheduleDto dto) {
		return new MedicationInfo(
			dto.medicationType().name(),
			dto.medicationName(),
			dto.intakeTime()
		);
	}
}
