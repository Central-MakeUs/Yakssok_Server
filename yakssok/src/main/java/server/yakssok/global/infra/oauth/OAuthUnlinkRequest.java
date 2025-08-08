package server.yakssok.global.infra.oauth;

import lombok.Builder;

@Builder
public record OAuthUnlinkRequest(
	String providerId,
	String refreshToken
) {
}
