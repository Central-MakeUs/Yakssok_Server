package server.yakssok.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import server.yakssok.domain.BaseEntity;

@Entity
public class User extends BaseEntity {

	@Id @GeneratedValue
	private Long id;

	private String nickName;
	private String profileImageUrl;
	@Column(length = 2000, unique = true)
	private String providerId;

	@Enumerated(EnumType.STRING)
	private Provider provider;

	private boolean pushAgreement;

	@Column(length = 500)
	private String fcmToken;
}
