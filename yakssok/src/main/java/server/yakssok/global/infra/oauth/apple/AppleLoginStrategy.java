package server.yakssok.global.infra.oauth.apple;



import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.global.infra.oauth.OAuthStrategy;
import server.yakssok.global.infra.oauth.exception.OAuthErrorCode;
import server.yakssok.global.infra.oauth.exception.OAuthException;

@Component
public class AppleLoginStrategy implements OAuthStrategy {
	@Override
	public AppleUserResponse fetchUserInfo(String socialAuthorizationCode) {
		try {
			DecodedJWT decoded = JWT.decode(socialAuthorizationCode);
			String sub = decoded.getSubject();
			return new AppleUserResponse(sub);
		} catch (JWTDecodeException e) {
			throw new OAuthException(OAuthErrorCode.INVALID_OAUTH_TOKEN);
		}
	}

	@Override
	public OAuthType getOAuthType() {
		return OAuthType.APPLE;
	}
}
