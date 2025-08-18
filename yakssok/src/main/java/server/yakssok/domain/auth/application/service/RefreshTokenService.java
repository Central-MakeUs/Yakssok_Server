package server.yakssok.domain.auth.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.domain.entity.RefreshToken;
import server.yakssok.domain.auth.domain.repository.RefreshTokenRepository;
import server.yakssok.domain.user.domain.entity.User;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;

	public void registerRefreshToken(User user, String refreshToken) {
		refreshTokenRepository.findByUserId(user.getId())
			.ifPresentOrElse(
				existing -> existing.updateToken(refreshToken),
				() -> refreshTokenRepository.save(new RefreshToken(user, refreshToken))
			);
	}

	public Optional<RefreshToken> findRefreshToken(Long userId) {
		return refreshTokenRepository.findByUserId(userId);
	}

	public void deleteRefreshToken(Long userId) {
		refreshTokenRepository.deleteAllByUserId(userId);
	}
}
