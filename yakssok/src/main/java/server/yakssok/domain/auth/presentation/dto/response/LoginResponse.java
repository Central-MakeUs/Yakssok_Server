package server.yakssok.domain.auth.presentation.dto.response;


public record LoginResponse(
	String accessToken,
	String refreshToken,
	Boolean isInitialized
) {

}