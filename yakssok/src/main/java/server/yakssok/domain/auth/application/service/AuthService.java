package server.yakssok.domain.auth.application.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.global.infra.oauth.OAuthStrategy;
import server.yakssok.global.infra.oauth.OAuthStrategyFactory;
import server.yakssok.global.infra.oauth.OAuthUserResponse;
import server.yakssok.domain.auth.application.exception.AuthErrorCode;
import server.yakssok.domain.auth.application.exception.AuthException;
import server.yakssok.domain.auth.domain.entity.RefreshToken;
import server.yakssok.domain.auth.presentation.dto.request.JoinRequest;
import server.yakssok.domain.auth.presentation.dto.response.LoginResponse;
import server.yakssok.domain.auth.presentation.dto.request.SocialLoginRequest;
import server.yakssok.domain.auth.presentation.dto.response.ReissueResponse;
import server.yakssok.domain.user.repository.UserRepository;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.exception.UserErrorCode;
import server.yakssok.global.common.jwt.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final JwtTokenUtils jwtTokenUtils;
	private final RefreshTokenService refreshTokenService;
	private final OAuthStrategyFactory strategyFactory;

	@Transactional
	public void join(JoinRequest joinRequest) {
		String oauthType = joinRequest.oauthType();
		String oauthAuthorizationCode = joinRequest.oauthAuthorizationCode();
		OAuthUserResponse socialUserResponse = getSocialUserResponse(oauthType, oauthAuthorizationCode);

		String providerId = socialUserResponse.getId();
		String profileImageUrl = socialUserResponse.getProfileImageUrl();
		checkDuplicateUser(oauthType, providerId);
		User user = joinRequest.toUser(providerId, profileImageUrl);
		userRepository.save(user);
	}

	private void checkDuplicateUser(String socialType, String providerId) {
		boolean isExist = userRepository.existsUserByProviderId(OAuthType.from(socialType), providerId);
		if(isExist) {
			throw new AuthException(AuthErrorCode.DUPLICATE_USER);
		}
	}


	@Transactional
	public LoginResponse login(SocialLoginRequest socialLoginRequest) {
		String oauthType = socialLoginRequest.oauthType();
		String oauthAuthorizationCode = socialLoginRequest.oauthAuthorizationCode();
		OAuthUserResponse socialUserResponse = getSocialUserResponse(oauthType, oauthAuthorizationCode);
		String providerId = socialUserResponse.getId();
		User user = findUser(oauthType, providerId);

		String accessToken = jwtTokenUtils.generateAccessToken(user.getId());
		String refreshToken = jwtTokenUtils.generateRefreshToken(user.getId());
		refreshTokenService.registerRefreshToken(user.getId(), refreshToken);
		return new LoginResponse(accessToken, refreshToken);
	}

	private OAuthUserResponse getSocialUserResponse(String socialType, String socialAuthorizationCode) {
		OAuthStrategy strategy = strategyFactory.getStrategy(socialType);
		return strategy.fetchUserInfo(socialAuthorizationCode);
	}


	private User findUser(String oauthType, String providerId) {
		User user = userRepository.findUserByProviderId(OAuthType.from(oauthType), providerId)
			.orElseThrow(() -> new AuthException(UserErrorCode.NOT_FOUND_USER));
		return user;
	}

	@Transactional
	public ReissueResponse reissue(String refreshToken) {
		Long userId = jwtTokenUtils.getIdFromJwt(refreshToken);
		RefreshToken savedRefreshToken = refreshTokenService.findRefreshToken(userId)
			.orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_JWT));
		if (!savedRefreshToken.isSame(refreshToken)) {
			throw new AuthException(AuthErrorCode.INVALID_JWT);
		}

		String accessToken = jwtTokenUtils.generateAccessToken(userId);
		return new ReissueResponse(accessToken);
	}

	@Transactional
	public void logOut(Long userId) {
		refreshTokenService.findRefreshToken(userId)
			.orElseThrow(() -> {throw new AuthException(AuthErrorCode.INVALID_JWT);
		});
		refreshTokenService.deleteRefreshToken(userId);
	}
}
