package server.yakssok.domain.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ReissueRequest(
	@Schema(description = "refresh token", example = "1234567890abcdef")
	@NotNull
	String refreshToken
) {
}
