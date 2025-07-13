package server.yakssok.domain.friend.presentation.dto.response;

import java.util.List;

public record FriendInfoGroupResponse(
	List<FriendInfoResponse> friendInfoResponses
) {
	public static FriendInfoGroupResponse of(List<FriendInfoResponse> friendInfoResponses) {
		return new FriendInfoGroupResponse(friendInfoResponses);
	}
}
