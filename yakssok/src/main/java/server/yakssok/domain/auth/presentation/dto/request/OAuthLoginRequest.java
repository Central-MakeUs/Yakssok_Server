package server.yakssok.domain.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record OAuthLoginRequest(
	@Schema(description = "카카오 Access Token/ 애플 authorization code", example = "1234567890abcdef")
	@NotNull
	String oauthAuthorizationCode,

	@Schema(description = "oauth 타입(apple/kakao)", example = "kakao")
	@NotNull
	String oauthType,

	@Schema(description = "apple nonce", example = "1234567890abcdef")
	String nonce
) {
}
