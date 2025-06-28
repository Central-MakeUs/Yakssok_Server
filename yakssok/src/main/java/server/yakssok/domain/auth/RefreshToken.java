package server.yakssok.domain.auth;

import jakarta.persistence.Entity;

@Entity
public class RefreshToken {
	private Long id;
	private String refreshToken;
}
