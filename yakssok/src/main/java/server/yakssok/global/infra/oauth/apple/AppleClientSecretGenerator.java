package server.yakssok.global.infra.oauth.apple;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppleClientSecretGenerator {

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
}
