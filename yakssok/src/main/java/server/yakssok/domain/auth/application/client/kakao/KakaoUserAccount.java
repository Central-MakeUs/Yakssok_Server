package server.yakssok.domain.auth.application.client.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserAccount(
	@JsonProperty("profile")
	KakaoProfile profile
) {

	public String profileImageUrl() {
		return profile.profileImageUrl();
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record KakaoProfile(
		@JsonProperty("profile_image_url")
		String profileImageUrl
	) {
	}
}