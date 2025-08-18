package server.yakssok.domain.friend.presentation.dto.response;

import java.util.List;

public record FollowingMedicationStatusGroupResponse(
	List<FollowingMedicationStatusResponse> followingMedicationStatusResponses
) {
	public static FollowingMedicationStatusGroupResponse of(
		List<FollowingMedicationStatusResponse> followingMedicationStatusResponses
	) {
		return new FollowingMedicationStatusGroupResponse(followingMedicationStatusResponses);
	}
}
