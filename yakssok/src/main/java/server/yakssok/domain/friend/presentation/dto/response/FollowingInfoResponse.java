package server.yakssok.domain.friend.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import server.yakssok.domain.friend.domain.entity.Friend;

public record FollowingInfoResponse(
	@Schema(description = "지인 ID", example = "1")
	Long userId,
	@Schema(description = "관계명", example = "엄마")
	String relationName,
	@Schema(description = "지인 프로필 이미지 URL", example = "https://example.com/profile.jpg")
	String profileImageUrl,
	@Schema(description = "닉네임", example = "홍길동")
	String nickName
) {
	public static FollowingInfoResponse from(Friend friend) {
		return new FollowingInfoResponse(
			friend.getFollowing().getId(),
			friend.getFollowing().getNickName(),
			friend.getFollowing().getProfileImageUrl(),
			friend.getFollowing().getNickName()
		);
	}
}
