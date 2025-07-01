package server.yakssok.domain.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SocialLoginRequest(
	@Schema(description = "카카오 Access Token/ 애플 authorization code", example = "1234567890abcdef")
	@NotNull
	String socialAuthorizationCode,

	@Schema(description = "소셜 타입(apple/kakao)", example = "apple")
	@NotNull
	String socialType
) {
}
