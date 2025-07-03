package server.yakssok.global.infra.oauth.kakao;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.global.infra.oauth.exception.OAuthErrorCode;
import server.yakssok.global.infra.oauth.OAuthStrategy;
import server.yakssok.domain.auth.application.exception.AuthException;

@Component
public class KakaoLoginStrategy implements OAuthStrategy {

	private final RestClient restClient;

	//TODO 링크 빼내기
	public KakaoLoginStrategy() {
		this.restClient = RestClient.builder()
			.baseUrl("https://kapi.kakao.com")
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.build();
	}

	@Override
	public KakaoUserResponse fetchUserInfo(String socialAuthorizationCode) {
		try {
			return restClient.get()
				.uri("/v2/user/me")
				.header(HttpHeaders.AUTHORIZATION, authHeaderValue(socialAuthorizationCode))
				.retrieve()
				.body(KakaoUserResponse.class);
		} catch (Exception e) {
			throw new AuthException(OAuthErrorCode.INVALID_OAUTH_TOKEN);
		}

	}

	@Override
	public OAuthType getOAuthType() {
		return OAuthType.KAKAO;
	}

	private String authHeaderValue(String accessToken) {
		return "Bearer %s".formatted(accessToken);
	}
}
