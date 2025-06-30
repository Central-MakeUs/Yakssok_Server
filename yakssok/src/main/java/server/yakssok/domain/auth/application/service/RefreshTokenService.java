package server.yakssok.domain.auth.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.domain.entity.RefreshToken;
import server.yakssok.domain.auth.domain.repository.RefreshTokenRepository;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;

	public void registerRefreshToken(Long userId, String refreshToken) {
		refreshTokenRepository.findById(userId)
			.ifPresentOrElse(
				existing -> existing.updateToken(refreshToken),
				() -> refreshTokenRepository.save(new RefreshToken(userId, refreshToken))
			);
	}

	public Optional<RefreshToken> findRefreshToken(Long userId) {
		return refreshTokenRepository.findById(userId);
	}

	public void deleteRefreshToken(Long userId) {
		refreshTokenRepository.deleteByUserId(userId);
	}
}
