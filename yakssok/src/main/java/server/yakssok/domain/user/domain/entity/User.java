package server.yakssok.domain.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
	private Provider provider;
	@Column(length = 500, unique = true)
	private String providerId;


	private boolean pushAgreement;

	@Column(length = 500)
	private String fcmToken;

	private User(String nickName, String profileImageUrl, Provider provider, String providerId, boolean pushAgreement, String fcmToken) {
		this.nickName = nickName;
		this.profileImageUrl = profileImageUrl;
		this.provider = provider;
		this.providerId = providerId;
		this.pushAgreement = pushAgreement;
		this.fcmToken = fcmToken;
	}

	public static User create(String nickName, String profileImageUrl, String providerName, String providerId, boolean pushAgreement, String fcmToken) {
		return new User(
			nickName,
			profileImageUrl,
			Provider.from(providerName),
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
}
