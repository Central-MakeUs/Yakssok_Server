package server.yakssok.domain.follow.presentation.dto.response;

import java.util.List;

public record FollowInfoGroupResponse<T>(
	List<T> followInfoResponses
) {
	public static FollowInfoGroupResponse<FollowInfoResponse> of(List<FollowInfoResponse> followInfoResponses) {
		return new FollowInfoGroupResponse<>(followInfoResponses);
	}
}
