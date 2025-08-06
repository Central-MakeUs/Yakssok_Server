package server.yakssok.global.infra.oauth.apple;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.exception.GlobalException;
import server.yakssok.global.infra.oauth.exception.OAuthException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppleJwtUtils {
	private static final long EXPIRATION_SECONDS = 600L;

	public static String createClientSecret(
		String teamId,
		String clientId,
		String keyId,
		String audience,
		String privateKeyPem
	) {
		ECPrivateKey privateKey = parsePrivateKey(privateKeyPem);
		Algorithm algorithm = Algorithm.ECDSA256(null, privateKey);
		return buildJwt(teamId, clientId, keyId, audience, algorithm);
	}


	private static ECPrivateKey parsePrivateKey(String pem) {
		try {
			String normalized = pem
				.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s+", "");
			byte[] decoded = Base64.getDecoder().decode(normalized);
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
			KeyFactory factory = KeyFactory.getInstance("EC");
			return (ECPrivateKey) factory.generatePrivate(spec);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to parse Apple private key PEM.", e);
		}
	}


	private static String buildJwt(
		String teamId,
		String clientId,
		String keyId,
		String audience,
		Algorithm algorithm
	) {
		long now = Instant.now().getEpochSecond();
		Date issuedAt = new Date(now * 1000);
		Date expiresAt = new Date((now + EXPIRATION_SECONDS) * 1000);

		return JWT.create()
			.withIssuer(teamId)
			.withIssuedAt(issuedAt)
			.withExpiresAt(expiresAt)
			.withAudience(audience)
			.withSubject(clientId)
			.withKeyId(keyId)
			.sign(algorithm);
	}

	public static DecodedJWT verifyIdToken(
		String idToken,
		String jwkUrl,
		String issuer,
		String audience,
		String nonceClaimKey,
		String expectedNonce
	) {
		try {
			DecodedJWT decoded = JWT.decode(idToken);
			RSAPublicKey key = fetchPublicKey(jwkUrl, decoded.getKeyId());
			JWTVerifier verifier = JWT.require(Algorithm.RSA256(key, null))
				.withIssuer(issuer)
				.withAudience(audience)
				.build();
			DecodedJWT jwt = verifier.verify(idToken);
			String actual = jwt.getClaim(nonceClaimKey).asString();
			if (expectedNonce != null && !expectedNonce.equals(actual)) {
				throw new OAuthException(ErrorCode.INVALID_OAUTH_TOKEN);
			}
			return jwt;
		} catch (JwkException|MalformedURLException e) {
			throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
		} catch (JWTVerificationException e) {
			throw new OAuthException(ErrorCode.INVALID_OAUTH_TOKEN);
		}
	}

	private static RSAPublicKey fetchPublicKey(String jwkUrl, String keyId) throws JwkException, MalformedURLException {
		JwkProvider provider = new UrlJwkProvider(new URL(jwkUrl));
		Jwk jwk = provider.get(keyId);
		return (RSAPublicKey) jwk.getPublicKey();
	}
}
