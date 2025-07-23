package server.yakssok.domain.user.domain.entity;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "users_device")
public class UserDevice {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "user_device_id")
	private Long id;

	@Column(length = 1000)
	private String fcmToken;

	@Column(nullable = false)
	private boolean alertOn;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	public static UserDevice createUserDevice(boolean alertOn, User user) {
		return new UserDevice(alertOn, user);
	}

	public UserDevice(boolean alertOn, User user) {
		this.alertOn = alertOn;
		this.user = user;
	}

	public void updateFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}
}
