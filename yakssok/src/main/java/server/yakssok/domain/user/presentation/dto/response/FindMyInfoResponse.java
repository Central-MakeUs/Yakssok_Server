package server.yakssok.domain.user.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record FindMyInfoResponse(
	@Schema(description = "닉네임", example = "노을")
	String nickname,
	@Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
	String profileImageUrl
) {
}
