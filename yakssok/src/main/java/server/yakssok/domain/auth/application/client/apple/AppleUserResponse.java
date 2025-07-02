package server.yakssok.domain.auth.application.client.apple;

import server.yakssok.domain.auth.application.client.SocialUserResponse;

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
