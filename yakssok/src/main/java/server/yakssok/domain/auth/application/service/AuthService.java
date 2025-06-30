package server.yakssok.domain.auth.application.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.application.client.kakao.KakaoLoginStrategy;
import server.yakssok.domain.auth.application.client.SocialLoginStrategy;
import server.yakssok.domain.auth.application.client.kakao.KakaoUserResponse;
import server.yakssok.domain.auth.application.exception.AuthException;
import server.yakssok.domain.auth.presentation.dto.request.JoinRequest;
import server.yakssok.domain.auth.presentation.dto.response.LoginResponse;
import server.yakssok.domain.auth.presentation.dto.request.SocialLoginRequest;
import server.yakssok.domain.user.UserRepository;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.exception.UserErrorCode;
import server.yakssok.global.common.jwt.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class AuthService {
	private SocialLoginStrategy strategy = new KakaoLoginStrategy();
	private final UserRepository userRepository;
	private final JwtTokenUtils jwtTokenUtils;

	@Transactional
	public String join(@Valid JoinRequest joinRequest) {
		KakaoUserResponse kakaoUserResponse = strategy.fetchUserInfo(joinRequest.socialAccessToken());
		User user = joinRequest.toUser(kakaoUserResponse.id()); //TODO : 태그 붙여서 저장하기
		userRepository.save(user); //TODO : 프로필 링크 주의
		return String.valueOf(user.getId());
	}

	@Transactional
	public LoginResponse login(@Valid SocialLoginRequest socialLoginRequest) {
		KakaoUserResponse kakaoUserResponse = strategy.fetchUserInfo(socialLoginRequest.socialAccessToken());
		User user = userRepository.findByProviderId(kakaoUserResponse.id())
			.orElseThrow(() -> new AuthException(UserErrorCode.NOT_FOUND_USER));

		String accessToken = jwtTokenUtils.generateAccessToken(user.getId());
		String refreshToken = jwtTokenUtils.generateRefreshToken(user.getId());
		return new LoginResponse(accessToken, refreshToken);
	}
}
