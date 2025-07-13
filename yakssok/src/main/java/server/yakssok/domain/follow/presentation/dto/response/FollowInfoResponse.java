package server.yakssok.domain.follow.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record FollowInfoResponse(
	@Schema(description = "지인 ID", example = "1")
	Long userId,
	@Schema(description = "관계명", example = "엄마")
	String relationName,
	@Schema(description = "지인 프로필 이미지 URL", example = "https://example.com/profile.jpg")
	String profileImageUrl
) {
}
