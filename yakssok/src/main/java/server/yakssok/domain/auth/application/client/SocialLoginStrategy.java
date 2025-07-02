package server.yakssok.domain.auth.application.client;

import server.yakssok.domain.user.domain.entity.Provider;

public interface SocialLoginStrategy {
	SocialUserResponse fetchUserInfo(String socialAuthorizationCode);
	Provider getSocialType();
}
