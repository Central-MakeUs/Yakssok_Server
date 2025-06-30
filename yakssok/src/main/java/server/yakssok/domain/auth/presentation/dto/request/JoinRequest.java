package server.yakssok.domain.auth.presentation.dto.request;

import server.yakssok.domain.user.domain.entity.User;

public record JoinRequest(
	String socialAccessToken,
	String socialType,
	String nickName,
	boolean pushAgreement,
	String fcmToken
) {
	public User toUser(String providerId, String profileImageUrl) {
		return User.create(nickName, profileImageUrl, socialType, providerId, pushAgreement, fcmToken);
	}
}
