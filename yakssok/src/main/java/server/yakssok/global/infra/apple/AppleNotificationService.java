package server.yakssok.global.infra.apple;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.yakssok.domain.user.application.service.UserDeletionService;
import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.repository.UserRepository;
import server.yakssok.global.infra.oauth.apple.AppleOAuthProperties;

import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppleNotificationService {
	private final UserRepository userRepository;
	private final UserDeletionService userDeletionService;
	private final AppleOAuthProperties appleProps;
	private final JwkProvider appleJwkProvider;
	private final ObjectMapper objectMapper;

	@Transactional
	public void handle(String signedPayload) {
		DecodedJWT jwt = verifySignedPayload(signedPayload);

		// 만료 체크(Verifier가 기본적으로 exp 검증도 하지만, 방어적으로 두셔도 좋습니다)
		Date exp = jwt.getExpiresAt();
		if (exp != null && exp.before(new Date())) return;

		JsonNode events = readEvents(jwt);
		if (events == null || events.isNull()) return;

		String type = text(events, "type");
		String sub  = text(events, "sub");
		if (type == null || sub == null) return;

		// Apple Account 영구 삭제 이벤트만 “서비스 탈퇴”로 처리
		if (isAccountDelete(type)) {
			Optional<User> userOpt = userRepository.findUserByProviderId(OAuthType.APPLE, sub);
			userOpt.ifPresent(user -> {
				if (user.isActive()) { // 멱등 가드
					userDeletionService.deleteUser(user);
				}
			});
		}
	}

	private DecodedJWT verifySignedPayload(String signedPayload) {
		try {
			DecodedJWT decoded = JWT.decode(signedPayload);
			String kid = decoded.getKeyId();
			if (kid == null) throw new SecurityException("Missing kid");

			Jwk jwk = appleJwkProvider.get(kid);
			RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();

			Algorithm algorithm = Algorithm.RSA256(publicKey, null);

			JWTVerifier verifier = JWT.require(algorithm)
				.withIssuer(appleProps.apiBaseUrl())
				.withAudience(appleProps.clientId())
				.build();

			return verifier.verify(signedPayload); // ✅ 서명+표준 클레임 검증
		} catch (Exception e) {
			throw new SecurityException("Apple S2S verify failed", e);
		}
	}

	private JsonNode readEvents(DecodedJWT jwt) {
		try {
			var eventsClaim = jwt.getClaim("events");
			if (eventsClaim == null || eventsClaim.isNull()) return null;

			Object asObj = eventsClaim.as(Object.class);
			return objectMapper.valueToTree(asObj);
		} catch (Exception e) {
			return null;
		}
	}

	private boolean isAccountDelete(String type) {
		return "account-delete".equals(type)
			|| "account-deleted".equals(type)
			|| "account_delete".equals(type);
	}

	private String text(JsonNode node, String field) {
		JsonNode v = node.get(field);
		return (v == null || v.isNull()) ? null : v.asText();
	}
}
