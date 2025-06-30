package server.yakssok.domain.auth.application.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.application.client.kakao.KakaoLoginStrategy;
import server.yakssok.domain.auth.application.client.SocialLoginStrategy;
import server.yakssok.domain.auth.application.client.kakao.KakaoUserResponse;
import server.yakssok.domain.auth.application.exception.AuthErrorCode;
import server.yakssok.domain.auth.application.exception.AuthException;
import server.yakssok.domain.auth.domain.entity.RefreshToken;
import server.yakssok.domain.auth.domain.repository.RefreshTokenRepository;
import server.yakssok.domain.auth.presentation.dto.request.JoinRequest;
import server.yakssok.domain.auth.presentation.dto.response.LoginResponse;
import server.yakssok.domain.auth.presentation.dto.request.SocialLoginRequest;
import server.yakssok.domain.auth.presentation.dto.response.ReissueResponse;
import server.yakssok.domain.user.repository.UserRepository;
import server.yakssok.domain.user.domain.entity.Provider;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.exception.UserErrorCode;
import server.yakssok.global.common.jwt.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class AuthService {
	private SocialLoginStrategy strategy = new KakaoLoginStrategy();
	private final UserRepository userRepository;
	private final JwtTokenUtils jwtTokenUtils;
	private final RefreshTokenService refreshTokenService;

	@Transactional
	public void join(JoinRequest joinRequest) {
		KakaoUserResponse kakaoUserResponse = strategy.fetchUserInfo(joinRequest.socialAccessToken());
		boolean isExist = userRepository.existsUserByProviderId(Provider.KAKAO, kakaoUserResponse.id());
		if(isExist) {
			throw new AuthException(AuthErrorCode.DUPLICATE_USER);
		}
		User user = joinRequest.toUser(kakaoUserResponse.id(), kakaoUserResponse.kakaoAccount().profileImageUrl());
		userRepository.save(user);
	}

	@Transactional
	public LoginResponse login(SocialLoginRequest socialLoginRequest) {
		KakaoUserResponse kakaoUserResponse = strategy.fetchUserInfo(socialLoginRequest.socialAccessToken());
		User user = userRepository.findUserByProviderId(Provider.KAKAO, kakaoUserResponse.id())
			.orElseThrow(() -> new AuthException(UserErrorCode.NOT_FOUND_USER));

		String accessToken = jwtTokenUtils.generateAccessToken(user.getId());
		String refreshToken = jwtTokenUtils.generateRefreshToken(user.getId());
		refreshTokenService.registerRefreshToken(user.getId(), refreshToken);
		return new LoginResponse(accessToken, refreshToken);
	}

	@Transactional
	public ReissueResponse reissue(String refreshToken) {
		Long userId = jwtTokenUtils.getIdFromJwt(refreshToken);
		refreshTokenService.findRefreshToken(userId)
			.orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_JWT));
		String accessToken = jwtTokenUtils.generateAccessToken(userId);
		System.out.println("accessToken = " + accessToken);
		return new ReissueResponse(accessToken);
	}
}
