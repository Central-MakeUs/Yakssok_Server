package server.yakssok.domain.auth.presentation.dto.response;

public record JoinResponse(
	String accessToken,
	String refreshToken
) {
}
