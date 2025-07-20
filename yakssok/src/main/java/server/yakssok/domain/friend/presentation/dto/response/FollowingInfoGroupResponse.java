package server.yakssok.domain.friend.presentation.dto.response;

import java.util.List;

public record FollowingInfoGroupResponse(
	List<FollowingInfoResponse> followingInfoResponses
) {
	public static FollowingInfoGroupResponse of(List<FollowingInfoResponse> friendInfoResponses) {
		return new FollowingInfoGroupResponse(friendInfoResponses);
	}
}
