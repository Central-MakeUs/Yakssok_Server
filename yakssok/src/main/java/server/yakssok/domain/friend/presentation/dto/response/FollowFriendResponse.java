package server.yakssok.domain.friend.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import server.yakssok.domain.user.domain.entity.User;

public record FollowFriendResponse(
	@Schema(description = "닉네임", example = "노을")
	String nickname,
	@Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
	String profileImageUrl
) {

	public static FollowFriendResponse of(User following) {
		return new FollowFriendResponse(following.getNickName(), following.getProfileImageUrl());
	}
}
