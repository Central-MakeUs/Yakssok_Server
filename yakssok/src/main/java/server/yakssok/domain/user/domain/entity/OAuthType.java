package server.yakssok.domain.user.domain.entity;

import lombok.Getter;
import server.yakssok.global.infra.oauth.exception.OAuthErrorCode;
import server.yakssok.global.infra.oauth.exception.OAuthException;

@Getter
public enum OAuthType {
	KAKAO,
	APPLE;

	public static OAuthType from(String oauthType) {
		for (OAuthType provider : values()) {
			if (provider.toString().equalsIgnoreCase(oauthType)) {
				return provider;
			}
		}
		throw new OAuthException(OAuthErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
	}
}
