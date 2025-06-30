package server.yakssok.domain.auth.presentation.dto.request;

public record SocialLoginRequest(
	String socialAccessToken,
	String socialType
) {
}
