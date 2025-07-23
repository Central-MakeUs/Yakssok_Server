package server.yakssok.domain.auth.application.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.presentation.dto.request.OAuthLoginRequest;
import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.domain.user.application.exception.UserException;
import server.yakssok.domain.user.domain.entity.UserDevice;
import server.yakssok.domain.user.domain.repository.UserDeviceRepository;
import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.infra.oauth.OAuthStrategy;
import server.yakssok.global.infra.oauth.OAuthStrategyFactory;
import server.yakssok.global.infra.oauth.OAuthUserResponse;
import server.yakssok.domain.auth.application.exception.AuthException;
import server.yakssok.domain.auth.domain.entity.RefreshToken;
import server.yakssok.domain.auth.presentation.dto.request.JoinRequest;
import server.yakssok.domain.auth.presentation.dto.response.LoginResponse;
import server.yakssok.domain.auth.presentation.dto.response.ReissueResponse;
import server.yakssok.domain.user.domain.repository.UserRepository;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.global.common.jwt.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final UserDeviceRepository userDeviceRepository;
	private final JwtTokenUtils jwtTokenUtils;
	private final RefreshTokenService refreshTokenService;
	private final OAuthStrategyFactory strategyFactory;

	@Transactional
	public void join(JoinRequest joinRequest) {
		String oauthType = joinRequest.oauthType();
		String oauthAuthorizationCode = joinRequest.oauthAuthorizationCode();
		OAuthUserResponse oAuthUserResponse = getOAuthUserResponse(oauthType, oauthAuthorizationCode, joinRequest.nonce());

		String providerId = oAuthUserResponse.getId();
		String profileImageUrl = oAuthUserResponse.getProfileImageUrl();
		checkDuplicateUser(oauthType, providerId);

		User user = joinRequest.toUser(providerId, profileImageUrl);
		UserDevice userDevice = joinRequest.toUserDevice(user);
		userRepository.save(user);
		userDeviceRepository.save(userDevice);
	}

	private void checkDuplicateUser(String oauthType, String providerId) {
		boolean isExist = userRepository.existsUserByProviderId(OAuthType.from(oauthType), providerId);
		if(isExist) {
			throw new AuthException(ErrorCode.DUPLICATE_USER);
		}
	}


	@Transactional
	public LoginResponse login(OAuthLoginRequest oAuthLoginRequest) {
		String oauthType = oAuthLoginRequest.oauthType();
		String oauthAuthorizationCode = oAuthLoginRequest.oauthAuthorizationCode();
		OAuthUserResponse oAuthUserResponse = getOAuthUserResponse(oauthType, oauthAuthorizationCode, oAuthLoginRequest.nonce());
		String providerId = oAuthUserResponse.getId();
		User user = findUser(oauthType, providerId);

		String accessToken = jwtTokenUtils.generateAccessToken(user.getId());
		String refreshToken = jwtTokenUtils.generateRefreshToken(user.getId());
		refreshTokenService.registerRefreshToken(user, refreshToken);
		return new LoginResponse(accessToken, refreshToken);
	}

	private OAuthUserResponse getOAuthUserResponse(String oauthType, String oauthAuthorizationCode, String nonce) {
		OAuthStrategy strategy = strategyFactory.getStrategy(oauthType);
		return strategy.fetchUserInfo(oauthAuthorizationCode, nonce);
	}

	private User findUser(String oauthType, String providerId) {
		User user = userRepository.findUserByProviderId(OAuthType.from(oauthType), providerId)
			.orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
		return user;
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
}
