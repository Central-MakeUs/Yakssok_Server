package server.yakssok.domain.user.domain.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.yakssok.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nickName;
	private String profileImageUrl;

	@Enumerated(EnumType.STRING)
	private OAuthType oAuthType;
	@Column(length = 500, unique = true)
	private String providerId;

	private boolean pushAgreement;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "invite_code", unique = true, nullable = false, length = 20))
	private InviteCode inviteCode; //회원가입 시 생성

	@Column(length = 500)
	private String fcmToken;

	private User(String nickName, String profileImageUrl, OAuthType oAuthType, String providerId, boolean pushAgreement, String fcmToken, InviteCode inviteCode) {
		this.nickName = nickName;
		this.profileImageUrl = profileImageUrl;
		this.oAuthType = oAuthType;
		this.providerId = providerId;
		this.pushAgreement = pushAgreement;
		this.fcmToken = fcmToken;
		this.inviteCode = inviteCode;
	}

	public static User create(String nickName, String profileImageUrl, OAuthType oauthType, String providerId, boolean pushAgreement, String fcmToken) {
		return new User(
			nickName,
			profileImageUrl,
			oauthType,
			providerId,
			pushAgreement,
			fcmToken,
			InviteCode.generate()
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
