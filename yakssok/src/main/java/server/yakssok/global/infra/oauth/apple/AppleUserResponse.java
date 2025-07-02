package server.yakssok.global.infra.oauth.apple;

import server.yakssok.global.infra.oauth.SocialUserResponse;

public record AppleUserResponse(
	String sub
) implements SocialUserResponse {
	@Override
	public String getId() {
		return sub;
	}

	@Override
	public String getProfileImageUrl() {
		return null;
	}
}
