package server.yakssok.domain.friend.presentation.dto.response;

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

	@Schema(description = "피드백 타입", example = "NAG, PRAISE")
	String feedbackType,

	@Schema(description = "잔소리 대상의 안먹은 약 / 칭찬 대상의 먹은 약 개수", example = "1")
	Integer medicationCount,

	@Schema(description = "잔소리 대상의 안먹은 약 / 칭찬 대상의 먹은 약 상세 정보")
	List<FollowingMedicationStatusDetailResponse> medicationDetailResponses
) {
	/* ===== 공통 생성기 ===== */
	private static FollowingMedicationStatusResponse ofCommon(
		Friend friend,
		FeedbackType type,
		List<FollowingMedicationStatusDetailResponse> details
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
	/** 미복용: 상세 리스트 포함 (총 개수는 details의 notTakenCount 합) */
	public static FollowingMedicationStatusResponse ofNag(
		Friend friend,
		List<MedicationScheduleDto> notTakenDtos
	) {
		var following = friend.getFollowing();
		var detail = FollowingMedicationStatusDetailResponse.of(
			following.getNickName(),
			friend.getRelationName(),
			notTakenDtos
		);
		return ofCommon(friend, FeedbackType.NAG, List.of(detail));
	}

	/** 칭찬: 상세 포함(원한다면), 개수는 파라미터로 명시 */
	public static FollowingMedicationStatusResponse ofPraise(
		Friend friend,
		List<MedicationScheduleDto> takenDtos
	) {
		var following = friend.getFollowing();
		var detail = FollowingMedicationStatusDetailResponse.of(
			following.getNickName(),
			friend.getRelationName(),
			takenDtos
		);
		return ofCommon(friend, FeedbackType.PRAISE, List.of(detail));
	}
}
