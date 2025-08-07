package server.yakssok.global.infra.oauth.apple;

public record AppleTokenResponse(
	String access_token,
	String expires_in,
	String id_token,
	String refresh_token,
	String token_type
) {
}
