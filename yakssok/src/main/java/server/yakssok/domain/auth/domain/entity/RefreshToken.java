package server.yakssok.domain.auth.domain.entity;

import jakarta.persistence.Entity;

@Entity
public class RefreshToken {
	private Long id;
	private String refreshToken;
}
