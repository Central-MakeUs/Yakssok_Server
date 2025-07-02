package server.yakssok.global.infra.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

import server.yakssok.global.infra.oauth.SocialUserResponse;
public record KakaoUserResponse(
	String id,

	@JsonProperty("kakao_account")
	KakaoUserAccount kakaoAccount
)implements SocialUserResponse {
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getProfileImageUrl() {
		return kakaoAccount.profileImageUrl();
	}
}
