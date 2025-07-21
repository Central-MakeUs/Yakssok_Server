package server.yakssok.domain.friend.presentation.dto.response;

import java.util.List;

public record FollowerInfoGroupResponse(
	List<FollowerInfoResponse> followerInfoResponses
) {
	public static FollowerInfoGroupResponse of(List<FollowerInfoResponse> friendInfoResponses) {
		return new FollowerInfoGroupResponse(friendInfoResponses);
	}
}
