package server.yakssok.global.infra.oauth.apple;



import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.global.exception.GlobalErrorCode;
import server.yakssok.global.exception.GlobalException;
import server.yakssok.global.infra.oauth.OAuthStrategy;
import server.yakssok.global.infra.oauth.exception.OAuthErrorCode;
import server.yakssok.global.infra.oauth.exception.OAuthException;

@Component
public class AppleLoginStrategy implements OAuthStrategy {
	@Override
	public AppleUserResponse fetchUserInfo(String idToken) {
		try {
			DecodedJWT decodedJWT = verifyIdToken(idToken);
			String sub = decodedJWT.getSubject();
			return new AppleUserResponse(sub);
		} catch (JWTVerificationException e) {
			throw new OAuthException(OAuthErrorCode.INVALID_OAUTH_TOKEN);
		} catch (Exception e) {
			throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	private DecodedJWT verifyIdToken(String idToken){
		DecodedJWT decoded = JWT.decode(idToken);
		String kid = decoded.getKeyId();

		// 1. 공개 키 가져오기
		// 2. 서명 검증기 생성
		// 3. 검증 및 반환
		//TODO : client id 검증
		return decoded;
	}

	@Override
	public OAuthType getOAuthType() {
		return OAuthType.APPLE;
	}
}
