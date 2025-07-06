package server.yakssok.domain.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.yakssok.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nickName;
	private String profileImageUrl;

	@Enumerated(EnumType.STRING)
	private OAuthType oAuthType;
	@Column(length = 500, unique = true)
	private String providerId;

	private boolean pushAgreement;

	@Column(length = 500)
	private String fcmToken;
	@Column(unique = true)
	private String inviteCode;

	private User(String nickName, String profileImageUrl, OAuthType oAuthType, String providerId, boolean pushAgreement, String fcmToken) {
		this.nickName = nickName;
		this.profileImageUrl = profileImageUrl;
		this.oAuthType = oAuthType;
		this.providerId = providerId;
		this.pushAgreement = pushAgreement;
		this.fcmToken = fcmToken;
	}

	public static User create(String nickName, String profileImageUrl, String oauthType, String providerId, boolean pushAgreement, String fcmToken) {
		return new User(
			nickName,
			profileImageUrl,
			OAuthType.from(oauthType),
			providerId,
			pushAgreement,
			fcmToken
		);
	}

	public void updatePushAgreement(boolean pushAgreement) {
		this.pushAgreement = pushAgreement;
	}

	public void updateFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}

	public void updateInfo(String nickname, String profileImageUrl) {
		this.nickName = nickname;
		this.profileImageUrl = profileImageUrl;
	}
}
