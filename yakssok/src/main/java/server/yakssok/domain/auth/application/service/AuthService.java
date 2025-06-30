package server.yakssok.domain.auth.application.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.application.client.kakao.KakaoLoginStrategy;
import server.yakssok.domain.auth.application.client.SocialLoginStrategy;
import server.yakssok.domain.auth.application.client.kakao.KakaoUserResponse;
import server.yakssok.domain.auth.presentation.dto.request.JoinRequest;
import server.yakssok.domain.auth.presentation.dto.response.LoginResponse;
import server.yakssok.domain.auth.presentation.dto.request.SocialLoginRequest;
import server.yakssok.domain.user.UserRepository;
import server.yakssok.domain.user.domain.entity.User;

@Service
@RequiredArgsConstructor
public class AuthService {
	private SocialLoginStrategy strategy = new KakaoLoginStrategy();
	private final UserRepository userRepository;

	@Transactional
	public String join(@Valid JoinRequest joinRequest) {
		KakaoUserResponse kakaoUserResponse = strategy.fetchUserInfo(joinRequest.socialAccessToken());
		User user = joinRequest.toUser(kakaoUserResponse.id());
		userRepository.save(user);
		return String.valueOf(user.getId());
	}

	@Transactional
	public LoginResponse login(@Valid SocialLoginRequest socialLoginRequest) {
		return null;
	}
}
