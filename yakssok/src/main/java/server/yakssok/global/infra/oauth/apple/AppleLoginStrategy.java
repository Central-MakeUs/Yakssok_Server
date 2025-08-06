package server.yakssok.global.infra.oauth.apple;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

	private static final String REVOKE_URI = "/auth/revoke";
	private static final String TOKEN_TYPE_HINT = "refresh_token";
	private static final String FIELD_CLIENT_ID = "client_id";
	private static final String FIELD_CLIENT_SECRET = "client_secret";
	private static final String FIELD_TOKEN = "token";
	private static final String FIELD_TOKEN_TYPE_HINT = "token_type_hint";

	@Override
	public OAuthType getOAuthType() {
		return OAuthType.APPLE;
	}

	@Override
	public AppleUserResponse fetchUserInfo(String idToken, String expectedNonce) {
		DecodedJWT jwt = AppleJwtUtils.verifyIdToken(
			idToken,
			properties.jwkUrl(),
			properties.apiBaseUrl(),
			properties.clientId(),
			properties.nonceClaimKey(),
			expectedNonce
		);
		return new AppleUserResponse(jwt.getSubject());
	}

	@Override
	public void unlink(OAuthUnlinkRequest oAuthUnlinkRequest) {
		String refreshToken = oAuthUnlinkRequest.refreshToken();
		if (isBlank(refreshToken)) {
			throw new OAuthException(ErrorCode.OAUTH_UNLINK_FAILED);
		}
		try {
			String clientSecret = createClientSecret();
			MultiValueMap<String, String> body = buildRevokeRequestBody(clientSecret, refreshToken);
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

	private MultiValueMap<String, String> buildRevokeRequestBody(String clientSecret, String refreshToken) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add(FIELD_CLIENT_ID, properties.clientId());
		body.add(FIELD_CLIENT_SECRET, clientSecret);
		body.add(FIELD_TOKEN, refreshToken);
		body.add(FIELD_TOKEN_TYPE_HINT, TOKEN_TYPE_HINT);
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

	private boolean isBlank(String refreshToken) {
		return refreshToken == null || refreshToken.trim().isEmpty();
	}
}
