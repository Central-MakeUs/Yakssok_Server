package server.yakssok.global.infra.oauth.apple;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.infra.oauth.OAuthStrategy;
import server.yakssok.global.infra.oauth.OAuthUnlinkRequest;
import server.yakssok.global.infra.oauth.exception.OAuthException;

@Component
@RequiredArgsConstructor
public class AppleLoginStrategy implements OAuthStrategy {
	private final AppleOAuthProperties properties;

	private static final String TOKEN_URI = "/auth/token";
	private static final String REVOKE_URI = "/auth/revoke";
	private static final String GRANT_TYPE_AUTH_CODE = "authorization_code";
	private static final String TOKEN_TYPE_HINT = "refresh_token";

	@Override
	public OAuthType getOAuthType() {
		return OAuthType.APPLE;
	}

	@Override
	public AppleUserResponse fetchUserInfo(String authorizationCode, String expectedNonce) {
		AppleTokenResponse tokenResponse = requestTokenFromApple(authorizationCode);
		DecodedJWT jwt = AppleJwtUtils.verifyIdToken(
			tokenResponse.idToken(),
			properties.jwkUrl(),
			properties.apiBaseUrl(),
			properties.clientId(),
			properties.nonceClaimKey(),
			expectedNonce
		);
		return new AppleUserResponse(jwt.getSubject(), tokenResponse.refreshToken());
	}

	private AppleTokenResponse requestTokenFromApple(String authorizationCode) {
		try {
			MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
			body.add("client_id", properties.clientId());
			body.add("client_secret", createClientSecret());
			body.add("code", authorizationCode);
			body.add("grant_type", GRANT_TYPE_AUTH_CODE);

			RestClient restClient = RestClient.builder()
				.baseUrl(properties.apiBaseUrl())
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();

			AppleTokenResponse tokenResponse = restClient.post()
				.uri(TOKEN_URI)
				.body(body)
				.retrieve()
				.body(AppleTokenResponse.class);

			if (tokenResponse == null || tokenResponse.idToken() == null) {
				throw new OAuthException(ErrorCode.INVALID_OAUTH_TOKEN);
			}
			return tokenResponse;
		} catch (HttpClientErrorException e) {
			throw new OAuthException(ErrorCode.INVALID_OAUTH_TOKEN);
		}

	}

	@Override
	public void unlink(OAuthUnlinkRequest request) {
		String refreshToken = request.refreshToken();
		if (isBlank(refreshToken)) {
			throw new OAuthException(ErrorCode.OAUTH_UNLINK_FAILED);
		}
		try {
			MultiValueMap<String, String> body = buildRevokeRequestBody(refreshToken);
			sendRevokeRequest(body);
		} catch (Exception e) {
			throw new OAuthException(ErrorCode.OAUTH_UNLINK_FAILED);
		}
	}

	private String createClientSecret() {
		return AppleJwtUtils.createClientSecret(
			properties.teamId(),
			properties.clientId(),
			properties.keyId(),
			properties.apiBaseUrl(),
			properties.privateKey()
		);
	}

	private MultiValueMap<String, String> buildRevokeRequestBody(String refreshToken) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("client_id", properties.clientId());
		body.add("client_secret", createClientSecret());
		body.add("token", refreshToken);
		body.add("token_type_hint", TOKEN_TYPE_HINT);
		return body;
	}

	private void sendRevokeRequest(MultiValueMap<String, String> body) {
		RestClient restClient = RestClient.builder()
			.baseUrl(properties.apiBaseUrl())
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.build();

		var response = restClient.post()
			.uri(REVOKE_URI)
			.body(body)
			.retrieve()
			.toBodilessEntity();

		if (!response.getStatusCode().is2xxSuccessful()) {
			throw new OAuthException(ErrorCode.OAUTH_UNLINK_FAILED);
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
