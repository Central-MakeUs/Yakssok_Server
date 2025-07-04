package server.yakssok.global.infra.oauth.kakao;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.global.infra.oauth.exception.OAuthErrorCode;
import server.yakssok.global.infra.oauth.OAuthStrategy;
import server.yakssok.global.infra.oauth.exception.OAuthException;

@Component
public class KakaoLoginStrategy implements OAuthStrategy {
	private final RestClient restClient;
	private final KakaoOAuthProperties properties;

	public KakaoLoginStrategy(KakaoOAuthProperties properties, RestClient.Builder builder) {
		this.properties = properties;
		this.restClient = builder
			.baseUrl(properties.apiBaseUrl())
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.build();
	}

	@Override
	public KakaoUserResponse fetchUserInfo(String socialAuthorizationCode, String nonce) {
		try {
			return restClient.get()
				.uri(properties.userInfoPath())
				.header(HttpHeaders.AUTHORIZATION, authHeaderValue(socialAuthorizationCode))
				.retrieve()
				.body(KakaoUserResponse.class);
		} catch (Exception e) {
			throw new OAuthException(OAuthErrorCode.INVALID_OAUTH_TOKEN);
		}

	}

	@Override
	public OAuthType getOAuthType() {
		return OAuthType.KAKAO;
	}

	private String authHeaderValue(String accessToken) {
		return properties.tokenTypeFormat().formatted(accessToken);
	}
}
