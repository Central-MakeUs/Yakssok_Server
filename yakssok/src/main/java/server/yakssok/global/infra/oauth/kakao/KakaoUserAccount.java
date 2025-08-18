package server.yakssok.global.infra.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


public record KakaoUserAccount(
	@JsonProperty("profile")
	KakaoProfile profile
) {
	public String profileImageUrl() {
		if (profile.isDefaultImage()) {
			return null;
		}
		String url = profile.profileImageUrl();
		return url.startsWith("http://")
			? url.replaceFirst("http://", "https://")
			: url;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record KakaoProfile(
		@JsonProperty("profile_image_url")
		String profileImageUrl,

		@JsonProperty("is_default_image")
		boolean isDefaultImage
	) {
	}
}