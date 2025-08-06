package server.yakssok.global.infra.oauth.kakao;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.infra.oauth.OAuthStrategy;
import server.yakssok.global.infra.oauth.OAuthUnlinkRequest;
import server.yakssok.global.infra.oauth.exception.OAuthException;

@Component
public class KakaoLoginStrategy implements OAuthStrategy {

	private static final String KAKAO_ADMIN_HEADER_PREFIX = "KakaoAK ";
	private static final String CONTENT_TYPE_FORM = MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8";
	private static final String UNLINK_BODY_FORMAT = "target_id_type=user_id&target_id=%s";

	private final RestClient restClient;
	private final KakaoOAuthProperties properties;

	public KakaoLoginStrategy(KakaoOAuthProperties properties, RestClient.Builder builder) {
		this.properties = properties;
		this.restClient = builder
			.baseUrl(properties.apiBaseUrl())
			.defaultHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_FORM)
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
			throw new OAuthException(ErrorCode.INVALID_OAUTH_TOKEN);
		}
	}

	@Override
	public OAuthType getOAuthType() {
		return OAuthType.KAKAO;
	}

	@Override
	public void unlink(OAuthUnlinkRequest oAuthUnlinkRequest) {
		String adminKey = properties.adminKey();
		String kakaoUserId = oAuthUnlinkRequest.providerId();
		String body = UNLINK_BODY_FORMAT.formatted(kakaoUserId);
		try {
			restClient.post()
				.uri(properties.unlinkPath())
				.header(HttpHeaders.AUTHORIZATION, KAKAO_ADMIN_HEADER_PREFIX + adminKey)
				.header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_FORM)
				.body(body)
				.retrieve()
				.toBodilessEntity();
		} catch (Exception e) {
			throw new OAuthException(ErrorCode.OAUTH_UNLINK_FAILED);
		}
	}

	private String authHeaderValue(String accessToken) {
		return properties.tokenTypeFormat().formatted(accessToken);
	}
}
