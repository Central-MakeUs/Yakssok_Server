package server.yakssok.domain.auth.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.yakssok.domain.user.domain.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	private String refreshToken;

	public RefreshToken(User user, String refreshToken) {
		this.user = user;
		this.refreshToken = refreshToken;
	}

	public void updateToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public boolean isSame(String refreshToken) {
		return this.refreshToken.equals(refreshToken);
	}
}
