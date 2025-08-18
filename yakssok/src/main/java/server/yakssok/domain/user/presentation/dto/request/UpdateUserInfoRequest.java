package server.yakssok.domain.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record UpdateUserInfoRequest(
	@Schema(description = "수정할 닉네임", example = "노을")
	@NotEmpty
	String nickname,

	@Schema(description = "수정할 프로필 링크 (기본프로필은 null)", example = "https://example.com/profile.jpg")
	String profileImageUrl
) {
}
