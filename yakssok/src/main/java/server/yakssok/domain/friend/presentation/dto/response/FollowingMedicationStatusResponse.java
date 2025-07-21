package server.yakssok.domain.friend.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import server.yakssok.domain.friend.domain.entity.Friend;
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
	@Schema(description = "오늘 안 먹은 약 개수", example = "1")
	Integer notTakenCount
) {

	public static FollowingMedicationStatusResponse of(Friend friend, int remainingMedicationCount) {
		User following = friend.getFollowing();
		return new FollowingMedicationStatusResponse(
			following.getId(),
			following.getNickName(),
			friend.getRelationName(),
			following.getProfileImageUrl(),
			remainingMedicationCount
		);
	}
}
