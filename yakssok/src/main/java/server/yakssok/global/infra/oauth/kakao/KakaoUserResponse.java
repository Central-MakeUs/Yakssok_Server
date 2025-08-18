package server.yakssok.global.infra.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

import server.yakssok.global.infra.oauth.OAuthUserResponse;
public record KakaoUserResponse(
	String id,

	@JsonProperty("kakao_account")
	KakaoUserAccount kakaoAccount
)implements OAuthUserResponse {
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getProfileImageUrl() {
		return kakaoAccount.profileImageUrl();
	}

	@Override
	public String getRefreshToken() {
		return null;
	}
}
