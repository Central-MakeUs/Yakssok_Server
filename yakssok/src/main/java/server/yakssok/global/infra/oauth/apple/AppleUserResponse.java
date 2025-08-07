package server.yakssok.global.infra.oauth.apple;

import server.yakssok.global.infra.oauth.OAuthUserResponse;

public record AppleUserResponse(
	String sub,
	String refreshToken
) implements OAuthUserResponse {

	@Override
	public String getId() {
		return sub;
	}
	@Override
	public String getProfileImageUrl() {
		return null;
	}

	@Override
	public String getRefreshToken() {
		return refreshToken;
	}
}
