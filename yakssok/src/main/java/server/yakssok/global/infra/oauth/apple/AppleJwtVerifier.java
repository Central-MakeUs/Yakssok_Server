package server.yakssok.global.infra.oauth.apple;


import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.infra.oauth.exception.OAuthException;

import java.security.interfaces.RSAPublicKey;

@Component
@RequiredArgsConstructor
public class AppleJwtVerifier {

	private final AppleOAuthProperties appleProps;
	private final JwkProvider appleJwkProvider;
	private final ObjectMapper objectMapper;

	/** 로그인 id_token 검증 */
	public DecodedJWT verifyIdToken(String idToken, String expectedNonce) {
		DecodedJWT jwt = verifyRsaJwt(idToken);

		if (expectedNonce != null) {
			String actualNonce = jwt.getClaim(appleProps.nonceClaimKey()).asString();
			if (!expectedNonce.equals(actualNonce)) {
				throw new OAuthException(ErrorCode.INVALID_OAUTH_TOKEN);
			}
		}
		return jwt;
	}

	/** S2S signedPayload 검증 */
	public DecodedJWT verifySignedPayload(String signedPayload) {
		return verifyRsaJwt(signedPayload);
	}

	/** S2S payload에서 events JsonNode 추출 */
	public JsonNode extractEvents(DecodedJWT jwt) {
		try {
			var claim = jwt.getClaim("events");
			if (claim == null || claim.isNull()) return null;

			Object asObj = claim.as(Object.class);
			return objectMapper.valueToTree(asObj);
		} catch (Exception e) {
			return null;
		}
	}

	private DecodedJWT verifyRsaJwt(String token) {
		try {
			DecodedJWT decoded = JWT.decode(token);
			String kid = decoded.getKeyId();
			if (kid == null) throw new OAuthException(ErrorCode.INVALID_OAUTH_TOKEN);

			Jwk jwk = appleJwkProvider.get(kid);
			RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();
			Algorithm algorithm = Algorithm.RSA256(publicKey, null);

			JWTVerifier verifier = JWT.require(algorithm)
				.withIssuer(appleProps.apiBaseUrl())
				.withAudience(appleProps.clientId())
				.build();

			return verifier.verify(token);
		} catch (Exception e) {
			throw new OAuthException(ErrorCode.INVALID_OAUTH_TOKEN);
		}
	}
}