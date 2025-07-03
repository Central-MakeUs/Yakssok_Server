package server.yakssok.global.infra.oauth;

import server.yakssok.domain.user.domain.entity.OAuthType;

public interface OAuthStrategy {
	OAuthUserResponse fetchUserInfo(String socialAuthorizationCode);
	OAuthType getOAuthType();
}
