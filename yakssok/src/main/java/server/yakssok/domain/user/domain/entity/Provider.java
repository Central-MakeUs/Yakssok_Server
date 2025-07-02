package server.yakssok.domain.user.domain.entity;

import lombok.Getter;
import server.yakssok.domain.auth.application.exception.AuthErrorCode;
import server.yakssok.domain.auth.application.exception.AuthException;

@Getter
public enum Provider {
	//카카오, 애플
	KAKAO,
	APPLE;


	public static Provider from(String providerName) {
		for (Provider provider : values()) {
			if (provider.toString().equalsIgnoreCase(providerName)) {
				return provider;
			}
		}
		throw new AuthException(AuthErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
	}
}
