package server.yakssok.domain.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import server.yakssok.domain.auth.presentation.dto.validator.ValidAppleJoinRequest;
import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.domain.user.domain.entity.User;

@ValidAppleJoinRequest
public record JoinRequest(
	@Schema(description = "카카오 Access Token/ 애플 authorization code", example = "1234567890abcdef")
	@NotNull
	String oauthAuthorizationCode,

	@Schema(description = "oauth 타입(apple/kakao)", example = "kakao")
	@NotNull
	String oauthType,

	@Schema(description = "apple nonce", example = "1234567890abcdef")
	String nonce,

	@Schema(description = "닉네임", example = "노을")
	@NotNull
	String nickName
) {
	public User toUser(String providerId, String profileImageUrl, String oAuthRefreshToken) {
		return User.create(nickName, profileImageUrl, OAuthType.from(oauthType), providerId, oAuthRefreshToken);
	}
}
