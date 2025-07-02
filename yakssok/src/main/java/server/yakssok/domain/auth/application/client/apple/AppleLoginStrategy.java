package server.yakssok.domain.auth.application.client.apple;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import server.yakssok.domain.auth.application.client.SocialLoginStrategy;
import server.yakssok.domain.user.domain.entity.Provider;
@Component
public class AppleLoginStrategy implements SocialLoginStrategy {
	@Override
	public AppleUserResponse fetchUserInfo(String socialAuthorizationCode) {
		DecodedJWT decoded = JWT.decode(socialAuthorizationCode);
		String sub = decoded.getSubject();
		return new AppleUserResponse(sub);
	}

	@Override
	public Provider getSocialType() {
		return Provider.APPLE;
	}
}
