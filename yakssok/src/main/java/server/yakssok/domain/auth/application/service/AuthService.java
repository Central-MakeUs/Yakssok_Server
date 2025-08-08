package server.yakssok.domain.auth.application.service;


import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.presentation.dto.request.OAuthLoginRequest;
import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.infra.oauth.OAuthStrategy;
import server.yakssok.global.infra.oauth.OAuthStrategyFactory;
import server.yakssok.global.infra.oauth.OAuthUnlinkRequest;
import server.yakssok.global.infra.oauth.OAuthUserResponse;
import server.yakssok.domain.auth.application.exception.AuthException;
import server.yakssok.domain.auth.domain.entity.RefreshToken;
import server.yakssok.domain.auth.presentation.dto.response.LoginResponse;
import server.yakssok.domain.auth.presentation.dto.response.ReissueResponse;
import server.yakssok.domain.user.domain.repository.UserRepository;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.global.common.jwt.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final JwtTokenUtils jwtTokenUtils;
	private final RefreshTokenService refreshTokenService;
	private final OAuthStrategyFactory strategyFactory;


	@Transactional
	public LoginResponse login(OAuthLoginRequest oAuthLoginRequest) {
		OAuthUserResponse oAuthUser = getOAuthUserResponse(
			oAuthLoginRequest.oauthType(),
			oAuthLoginRequest.oauthAuthorizationCode(),
			oAuthLoginRequest.nonce()
		);

		User user = userRepository.findUserByProviderId(
				OAuthType.from(oAuthLoginRequest.oauthType()), oAuthUser.getId()
			)
			.orElseGet(() -> joinUser(oAuthLoginRequest, oAuthUser));
		return generateLoginResponse(user);
	}

	@Transactional
	public ReissueResponse reissue(String refreshToken) {
		Long userId = jwtTokenUtils.getIdFromJwt(refreshToken);
		RefreshToken savedRefreshToken = refreshTokenService.findRefreshToken(userId)
			.orElseThrow(() -> new AuthException(ErrorCode.INVALID_JWT));
		if (!savedRefreshToken.isSame(refreshToken)) {
			throw new AuthException(ErrorCode.INVALID_JWT);
		}
		String accessToken = jwtTokenUtils.generateAccessToken(userId);
		return new ReissueResponse(accessToken);
	}

	@Transactional
	public void logOut(Long userId) {
		refreshTokenService.findRefreshToken(userId)
			.orElseThrow(() -> {throw new AuthException(ErrorCode.INVALID_JWT);
		});
		refreshTokenService.deleteRefreshToken(userId);
	}

	@Transactional
	public void unlinkOAuth(User user) {
		OAuthType oAuthType = user.getOAuthType();
		String providerId = user.getProviderId();
		String refreshToken = user.getOAuthRefreshToken();

		OAuthUnlinkRequest unlinkRequest = new OAuthUnlinkRequest(
			providerId,
			refreshToken
		);
		OAuthStrategy strategy = strategyFactory.getStrategy(oAuthType.name());
		strategy.unlink(unlinkRequest);
	}

	private User joinUser(OAuthLoginRequest oAuthLoginRequest, OAuthUserResponse oAuthUser) {
		User newUser = oAuthLoginRequest.toUser(
			oAuthUser.getId(),
			oAuthUser.getProfileImageUrl(),
			oAuthUser.getRefreshToken()
		);
		return userRepository.save(newUser);
	}

	private LoginResponse generateLoginResponse(User user) {
		String accessToken = jwtTokenUtils.generateAccessToken(user.getId());
		String refreshToken = jwtTokenUtils.generateRefreshToken(user.getId());
		refreshTokenService.registerRefreshToken(user, refreshToken);
		return new LoginResponse(accessToken, refreshToken);
	}

	private OAuthUserResponse getOAuthUserResponse(String oauthType, String oauthAuthorizationCode, String nonce) {
		OAuthStrategy strategy = strategyFactory.getStrategy(oauthType);
		return strategy.fetchUserInfo(oauthAuthorizationCode, nonce);
	}
}
