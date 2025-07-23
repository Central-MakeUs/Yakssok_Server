package server.yakssok.domain.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.entity.UserDevice;

public record JoinRequest(
	@Schema(description = "카카오 Access Token/ 애플 idToken", example = "1234567890abcdef")
	@NotNull
	String oauthAuthorizationCode,

	@Schema(description = "oauth 타입(apple/kakao)", example = "kakao")
	@NotNull
	String oauthType,

	@Schema(description = "apple nonce", example = "1234567890abcdef")
	String nonce,

	@Schema(description = "닉네임", example = "노을")
	@NotNull
	String nickName,

	@Schema(description = "푸시 알림 동의 여부", example = "true")
	@NotNull
	Boolean pushAgreement
) {
	public User toUser(String providerId, String profileImageUrl) {
		return User.create(nickName, profileImageUrl, OAuthType.from(oauthType), providerId);
	}

	public UserDevice toUserDevice(User user) {
		return UserDevice.createUserDevice(pushAgreement, user);
	}
}
