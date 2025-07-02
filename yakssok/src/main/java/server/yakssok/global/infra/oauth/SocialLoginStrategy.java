package server.yakssok.global.infra.oauth;

import server.yakssok.domain.user.domain.entity.Provider;

public interface SocialLoginStrategy {
	SocialUserResponse fetchUserInfo(String socialAuthorizationCode);
	Provider getSocialType();
}
