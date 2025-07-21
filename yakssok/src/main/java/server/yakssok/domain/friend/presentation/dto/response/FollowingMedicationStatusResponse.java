package server.yakssok.domain.friend.presentation.dto.response;

import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.user.domain.entity.User;

public record FollowingMedicationStatusResponse(
	Long userId,
	String nickName,
	String relationName,
	String profileImageUrl,
	Integer remainingMedicationCount
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
