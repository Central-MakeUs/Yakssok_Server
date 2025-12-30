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

	private String nickName;
	private String profileImageUrl;

	@Enumerated(EnumType.STRING)
	private OAuthType oAuthType;
	@Column(length = 500, unique = true)
	private String providerId;

	@Column(length = 500, unique = true)
	private String oAuthRefreshToken;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "invite_code", unique = true, length = 20))
	private InviteCode inviteCode;
	private boolean isDeleted;
	private boolean isInitialized;

	private User(String profileImageUrl, OAuthType oAuthType, String providerId, String oAuthRefreshToken, InviteCode inviteCode) {
		this.profileImageUrl = profileImageUrl;
		this.oAuthType = oAuthType;
		this.providerId = providerId;
		this.oAuthRefreshToken = oAuthRefreshToken;
		this.inviteCode = inviteCode;
	}

	public static User create(String profileImageUrl, OAuthType oauthType, String providerId, String oAuthRefreshToken) {
		return new User(
			profileImageUrl,
			oauthType,
			providerId,
			oAuthRefreshToken,
			InviteCode.generate()
		);
	}

	public void updateInfo(String nickname, String profileImageUrl) {
		this.nickName = nickname;
		this.profileImageUrl = profileImageUrl;
	}

	public void deactivate() {
		this.nickName = null;
		this.profileImageUrl = null;
		this.oAuthType = null;
		this.providerId = null;
		this.inviteCode = null;
		this.isDeleted = true;
		this.isInitialized = false;
		this.oAuthRefreshToken = null;
	}

	public boolean isActive() {
		return !isDeleted;
	}

	public void initializeUserInfo(String nickName) {
		this.nickName = nickName;
		this.isInitialized = true;
	}
}
