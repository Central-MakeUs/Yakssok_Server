package server.yakssok.domain.auth.application.client.kakao;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;

import server.yakssok.domain.auth.application.client.SocialLoginStrategy;

@Component
public class KakaoLoginStrategy implements SocialLoginStrategy {

	private final RestClient restClient;

	public KakaoLoginStrategy() {
		this.restClient = RestClient.builder()
			.baseUrl("https://kapi.kakao.com")
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.build();
	}

	@Override
	public KakaoUserResponse fetchUserInfo(String accessToken) {
		return restClient.get()
			.uri("/v2/user/me")
			.header(HttpHeaders.AUTHORIZATION, authHeaderValue(accessToken))
			.retrieve()
			.body(KakaoUserResponse.class);
	}

	private String authHeaderValue(String accessToken) {
		return "Bearer %s".formatted(accessToken);
	}
}
