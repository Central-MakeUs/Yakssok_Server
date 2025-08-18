package server.yakssok.domain.user.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import server.yakssok.domain.user.domain.entity.User;

public record FindUserInfoResponse(
	@Schema(description = "닉네임", example = "노을")
	String nickname,
	@Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
	String profileImageUrl
) {

	public static FindUserInfoResponse from(User user) {
		return new FindUserInfoResponse(user.getNickName(), user.getProfileImageUrl());
	}
}
