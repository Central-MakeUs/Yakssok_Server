package server.yakssok.global.infra.oauth.apple;




import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.global.exception.GlobalErrorCode;
import server.yakssok.global.exception.GlobalException;
import server.yakssok.global.infra.oauth.OAuthStrategy;
import server.yakssok.global.infra.oauth.exception.OAuthErrorCode;
import server.yakssok.global.infra.oauth.exception.OAuthException;

@Component
public class AppleLoginStrategy implements OAuthStrategy {
	@Override
	public AppleUserResponse fetchUserInfo(String idToken, String expectedNonce) {
		try {
			DecodedJWT decodedJWT = verifyIdToken(idToken, expectedNonce);
			String sub = decodedJWT.getSubject();
			return new AppleUserResponse(sub);
		} catch (JWTVerificationException e) {
			throw new OAuthException(OAuthErrorCode.INVALID_OAUTH_TOKEN);
		} catch (Exception e) {
			throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	private DecodedJWT verifyIdToken(String idToken, String expectedNonce) throws MalformedURLException, JwkException {
		DecodedJWT decoded = JWT.decode(idToken);
		RSAPublicKey publicKey = getApplePublicKey(decoded.getKeyId());

		JWTVerifier verifier = createVerifier(publicKey);
		DecodedJWT verified = verifier.verify(idToken);

		validateNonce(verified, expectedNonce);
		validateTokenExpiration(verified);

		return verified;
	}

	private RSAPublicKey getApplePublicKey(String kid) throws MalformedURLException, JwkException {
		JwkProvider jwkProvider = new UrlJwkProvider(new URL("https://appleid.apple.com/auth/keys"));
		Jwk jwk = jwkProvider.get(kid);
		return (RSAPublicKey) jwk.getPublicKey();
	}

	private JWTVerifier createVerifier(RSAPublicKey publicKey) {
		return JWT.require(Algorithm.RSA256(publicKey, null))
			.withIssuer("https://appleid.apple.com")
			// TODO: clientId 검증
			// .withAudience(clientId)
			.build();
	}

	private void validateNonce(DecodedJWT jwt, String expectedNonce) {
		String actualNonce = jwt.getClaim("nonce").asString();
		if (expectedNonce != null && !expectedNonce.equals(actualNonce)) {
			throw new OAuthException(OAuthErrorCode.INVALID_OAUTH_TOKEN);
		}
	}


	private void validateTokenExpiration(DecodedJWT jwt) {
		Date expiresAt = jwt.getExpiresAt();
		if (expiresAt != null && expiresAt.before(new Date())) {
			throw new OAuthException(OAuthErrorCode.INVALID_OAUTH_TOKEN);
		}
	}




	@Override
	public OAuthType getOAuthType() {
		return OAuthType.APPLE;
	}
}
