package server.yakssok.domain.auth.application.client.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

import server.yakssok.domain.auth.application.client.SocialUserResponse;
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
