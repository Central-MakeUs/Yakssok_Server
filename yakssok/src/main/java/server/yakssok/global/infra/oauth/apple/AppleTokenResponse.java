package server.yakssok.global.infra.oauth.apple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AppleTokenResponse(
	@JsonProperty("id_token")
	String idToken,
	@JsonProperty("refresh_token")
	String refreshToken
) {
}
