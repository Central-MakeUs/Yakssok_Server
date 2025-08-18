package server.yakssok.domain.user.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import server.yakssok.domain.user.domain.entity.User;

public record FindMyInfoResponse(
	@Schema(description = "닉네임", example = "노을")
	String nickname,
	@Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
	String profileImageUrl,
	@Schema(description = "내 복약 수", example = "5")
	Integer medicationCount,
	@Schema(description = "내 팔로잉 수", example = "10")
	Integer followingCount
) {
	public static FindMyInfoResponse of(
		User user,
		Integer medicationCount,
		Integer followingCount
	) {
		return new FindMyInfoResponse(user.getNickName(), user.getProfileImageUrl(), medicationCount, followingCount);
	}
}
