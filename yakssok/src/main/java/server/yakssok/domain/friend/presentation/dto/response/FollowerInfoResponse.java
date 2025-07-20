package server.yakssok.domain.friend.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import server.yakssok.domain.friend.domain.entity.Friend;

public record FollowerInfoResponse(
	@Schema(description = "지인 ID", example = "1")
	Long userId,
	@Schema(description = "지인 프로필 이미지 URL", example = "https://example.com/profile.jpg")
	String profileImageUrl,
	@Schema(description = "닉네임", example = "홍길동")
	String nickName
) {
	public static FollowerInfoResponse of(Friend friend) {
		return new FollowerInfoResponse(
			friend.getUser().getId(),
			friend.getUser().getProfileImageUrl(),
			friend.getUser().getNickName()
		);
	}
}
