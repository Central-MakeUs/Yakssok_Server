package server.yakssok.global.infra.apple.notification;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.yakssok.domain.user.application.service.UserDeletionService;
import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.repository.UserRepository;
import server.yakssok.global.infra.oauth.apple.AppleJwtVerifier;

@Service
@RequiredArgsConstructor
public class AppleNotificationService {

	private final UserRepository userRepository;
	private final UserDeletionService userDeletionService;
	private final AppleJwtVerifier appleJwtVerifier;

	@Transactional
	public void handle(String signedPayload) {
		DecodedJWT jwt = appleJwtVerifier.verifySignedPayload(signedPayload);
		JsonNode events = appleJwtVerifier.extractEvents(jwt);
		if (events == null || events.isNull()) return;

		String type = text(events, "type");
		String sub  = text(events, "sub");
		if (type == null || sub == null) return;

		if (isAccountDelete(type)) {
			userRepository.findUserByProviderId(OAuthType.APPLE, sub)
				.filter(User::isActive)
				.ifPresent(userDeletionService::deleteUser);
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
