package server.yakssok.global.infra.oauth.apple;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;

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

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.exception.GlobalException;
import server.yakssok.global.infra.oauth.OAuthStrategy;
import server.yakssok.global.infra.oauth.OAuthUnlinkRequest;
import server.yakssok.global.infra.oauth.exception.OAuthException;

@Component
@RequiredArgsConstructor
public class AppleLoginStrategy implements OAuthStrategy {
	private final AppleOAuthProperties properties;

	@Override
	public AppleUserResponse fetchUserInfo(String idToken, String expectedNonce) {
		try {
			DecodedJWT decodedJWT = validateIdToken(idToken, expectedNonce);
			String sub = decodedJWT.getSubject();
			return new AppleUserResponse(sub);
		} catch (JWTVerificationException e) {
			throw new OAuthException(ErrorCode.INVALID_OAUTH_TOKEN);
		} catch (JwkException | MalformedURLException e) {
			throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	private DecodedJWT validateIdToken(String idToken, String expectedNonce) throws MalformedURLException, JwkException {
		DecodedJWT decoded = JWT.decode(idToken);
		RSAPublicKey publicKey = getApplePublicKey(decoded.getKeyId());
		JWTVerifier verifier = createVerifier(publicKey);
		DecodedJWT verified = verifier.verify(idToken);
		validateNonce(verified, expectedNonce);
		return verified;
	}

	private RSAPublicKey getApplePublicKey(String kid) throws MalformedURLException, JwkException {
		JwkProvider jwkProvider = new UrlJwkProvider(new URL(properties.jwkUrl()));
		Jwk jwk = jwkProvider.get(kid);
		return (RSAPublicKey) jwk.getPublicKey();
	}

	private JWTVerifier createVerifier(RSAPublicKey publicKey) {
		return JWT.require(Algorithm.RSA256(publicKey, null))
			.withIssuer(properties.issuer())
			.withAudience(properties.clientId())
			.build();
	}

	private void validateNonce(DecodedJWT jwt, String expectedNonce) {
		String actualNonce = jwt.getClaim(properties.nonceClaimKey()).asString();
		if (expectedNonce != null && !expectedNonce.equals(actualNonce)) {
			throw new OAuthException(ErrorCode.INVALID_OAUTH_TOKEN);
		}
	}

	@Override
	public OAuthType getOAuthType() {
		return OAuthType.APPLE;
	}

	@Override
	public void unlink(OAuthUnlinkRequest oAuthUnlinkRequest) {

	}
}
