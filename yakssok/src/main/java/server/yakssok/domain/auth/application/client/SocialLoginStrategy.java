package server.yakssok.domain.auth.application.client;

import server.yakssok.domain.auth.application.client.kakao.KakaoUserResponse;

public interface SocialLoginStrategy {
	KakaoUserResponse fetchUserInfo(String socialAuthorizationCode);
}
