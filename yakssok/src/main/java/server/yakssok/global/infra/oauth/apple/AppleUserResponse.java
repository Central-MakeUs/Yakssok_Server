package server.yakssok.global.infra.oauth.apple;

import server.yakssok.global.infra.oauth.OAuthUserResponse;

public record AppleUserResponse(
	String sub
) implements OAuthUserResponse {
	@Override
	public String getId() {
		return sub;
	}

	@Override
	public String getProfileImageUrl() {
		return null;
	}
}
