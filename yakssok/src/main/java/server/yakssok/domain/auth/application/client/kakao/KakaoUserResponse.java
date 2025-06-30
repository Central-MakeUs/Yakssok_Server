package server.yakssok.domain.auth.application.client.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserResponse(
	String id,

	@JsonProperty("kakao_account")
	KakaoUserAccount kakaoAccount
) {
}
