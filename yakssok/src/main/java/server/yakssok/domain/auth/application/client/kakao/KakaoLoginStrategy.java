package server.yakssok.domain.auth.application.client.kakao;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


import server.yakssok.domain.auth.application.client.SocialLoginStrategy;
import server.yakssok.domain.auth.application.exception.AuthErrorCode;
import server.yakssok.domain.auth.application.exception.AuthException;
import server.yakssok.domain.user.domain.entity.Provider;

@Component
public class KakaoLoginStrategy implements SocialLoginStrategy {

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
			throw new AuthException(AuthErrorCode.INVALID_KAKAO_TOKEN);
		}

	}

	@Override
	public Provider getSocialType() {
		return Provider.KAKAO;
	}

	private String authHeaderValue(String accessToken) {
		return "Bearer %s".formatted(accessToken);
	}
}
