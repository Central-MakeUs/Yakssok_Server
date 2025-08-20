package server.yakssok.domain.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import server.yakssok.domain.auth.application.exception.AuthException;
import server.yakssok.domain.auth.domain.entity.RefreshToken;
import server.yakssok.domain.auth.presentation.controller.LogoutRequest;
import server.yakssok.domain.auth.presentation.dto.request.OAuthLoginRequest;
import server.yakssok.domain.auth.presentation.dto.response.LoginResponse;
import server.yakssok.domain.auth.presentation.dto.response.ReissueResponse;
import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.repository.UserDeviceRepository;
import server.yakssok.domain.user.domain.repository.UserRepository;
import server.yakssok.global.common.jwt.JwtTokenUtils;
import server.yakssok.global.infra.oauth.*;
import server.yakssok.global.infra.oauth.exception.OAuthException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock UserRepository userRepository;
	@Mock JwtTokenUtils jwtTokenUtils;
	@Mock RefreshTokenService refreshTokenService;
	@Mock OAuthStrategyFactory strategyFactory;
	@Mock UserDeviceRepository userDeviceRepository;
	@Mock OAuthStrategy oAuthStrategy;

	@InjectMocks AuthService authService;

	private OAuthLoginRequest mockLoginRequest(String oauthTypeUpperCase, String authorizationCode, String nonce) {
		OAuthLoginRequest request = mock(OAuthLoginRequest.class);
		when(request.oauthType()).thenReturn(oauthTypeUpperCase);
		when(request.oauthAuthorizationCode()).thenReturn(authorizationCode);
		when(request.nonce()).thenReturn(nonce);
		return request;
	}

	private OAuthUserResponse oAuthUserWithIdOnly(String providerId) {
		OAuthUserResponse response = mock(OAuthUserResponse.class);
		when(response.getId()).thenReturn(providerId);
		return response;
	}

	private OAuthUserResponse oAuthUserWithAllFields(String providerId, String profileImageUrl, String providerRefreshToken) {
		OAuthUserResponse response = mock(OAuthUserResponse.class);
		when(response.getId()).thenReturn(providerId);
		when(response.getProfileImageUrl()).thenReturn(profileImageUrl);
		when(response.getRefreshToken()).thenReturn(providerRefreshToken);
		return response;
	}

	@Nested
	@DisplayName("login")
	class Login {

		@Test
		@DisplayName("KAKAO: 기존 사용자면 가입 없이 토큰 발급 및 Refresh Token 저장")
		void login_existing_kakao_user() {
			OAuthLoginRequest request = mockLoginRequest("KAKAO", "auth-code", "nonce");
			OAuthUserResponse oAuthUserResponse = oAuthUserWithIdOnly("kakao-pid-123");

			when(strategyFactory.getStrategy("KAKAO")).thenReturn(oAuthStrategy);
			when(oAuthStrategy.fetchUserInfo("auth-code", "nonce")).thenReturn(oAuthUserResponse);

			User existingUser = mock(User.class);
			when(existingUser.getId()).thenReturn(7L);
			when(existingUser.isInitialized()).thenReturn(true);
			when(userRepository.findUserByProviderId(OAuthType.from("KAKAO"), "kakao-pid-123"))
				.thenReturn(Optional.of(existingUser));

			when(jwtTokenUtils.generateAccessToken(7L)).thenReturn("ACCESS_TOKEN");
			when(jwtTokenUtils.generateRefreshToken(7L)).thenReturn("REFRESH_TOKEN");

			LoginResponse response = authService.login(request);

			assertThat(response.accessToken()).isEqualTo("ACCESS_TOKEN");
			assertThat(response.refreshToken()).isEqualTo("REFRESH_TOKEN");
			assertThat(response.isInitialized()).isTrue();

			verify(refreshTokenService).registerRefreshToken(existingUser, "REFRESH_TOKEN");
			verify(userRepository, never()).save(any());
		}

		@Test
		@DisplayName("APPLE: 사용자가 없으면 가입 후 토큰 발급")
		void login_new_apple_user_joins() {
			OAuthLoginRequest request = mockLoginRequest("APPLE", "code", "nonce-1");
			OAuthUserResponse oAuthUserResponse = oAuthUserWithAllFields("apple-pid-9", "http://img2", "provider-rt-2");

			when(strategyFactory.getStrategy("APPLE")).thenReturn(oAuthStrategy);
			when(oAuthStrategy.fetchUserInfo("code", "nonce-1")).thenReturn(oAuthUserResponse);

			when(userRepository.findUserByProviderId(OAuthType.from("APPLE"), "apple-pid-9"))
				.thenReturn(Optional.empty());

			User newUser = mock(User.class);
			when(newUser.getId()).thenReturn(33L);
			when(newUser.isInitialized()).thenReturn(false);

			when(request.toUser("apple-pid-9", "http://img2", "provider-rt-2")).thenReturn(newUser);
			when(userRepository.save(newUser)).thenReturn(newUser);

			when(jwtTokenUtils.generateAccessToken(33L)).thenReturn("ACCESS_TOKEN_2");
			when(jwtTokenUtils.generateRefreshToken(33L)).thenReturn("REFRESH_TOKEN_2");

			LoginResponse response = authService.login(request);

			assertThat(response.accessToken()).isEqualTo("ACCESS_TOKEN_2");
			assertThat(response.refreshToken()).isEqualTo("REFRESH_TOKEN_2");
			assertThat(response.isInitialized()).isFalse();

			verify(refreshTokenService).registerRefreshToken(newUser, "REFRESH_TOKEN_2");
			verify(userRepository).save(newUser);
		}

		@Test
		@DisplayName("GOOGLE: 미지원 소셜이면 OAuthException 발생")
		void login_unsupported_google_fails() {
			OAuthLoginRequest request = mockLoginRequest("GOOGLE", "code", "nonce");

			when(strategyFactory.getStrategy("GOOGLE")).thenReturn(oAuthStrategy);

			assertThrows(OAuthException.class, () -> authService.login(request));

			verify(userRepository, never()).save(any());
			verify(refreshTokenService, never()).registerRefreshToken(any(), anyString());
			verify(jwtTokenUtils, never()).generateAccessToken(anyLong());
			verify(jwtTokenUtils, never()).generateRefreshToken(anyLong());
		}
	}

	@Nested
	@DisplayName("reissue")
	class Reissue {

		@Test
		@DisplayName("유효한 Refresh Token이고 저장된 Refresh Token과 일치하면 Access Token 재발급")
		void reissue_success() {
			String refreshTokenString = "valid-refresh-token";

			when(jwtTokenUtils.isValidateToken(refreshTokenString)).thenReturn(true);
			when(jwtTokenUtils.getIdFromJwt(refreshTokenString)).thenReturn(5L);

			RefreshToken savedRefreshToken = mock(RefreshToken.class);
			when(refreshTokenService.findRefreshToken(5L)).thenReturn(Optional.of(savedRefreshToken));
			when(savedRefreshToken.isSame(refreshTokenString)).thenReturn(true);

			when(jwtTokenUtils.generateAccessToken(5L)).thenReturn("new-access-token");

			ReissueResponse response = authService.reissue(refreshTokenString);

			assertThat(response.accessToken()).isEqualTo("new-access-token");
		}

		@Test
		@DisplayName("Refresh Token이 유효하지 않으면 예외")
		void reissue_invalid_refresh_token_signature_or_expiration() {
			when(jwtTokenUtils.isValidateToken("bad-refresh-token")).thenReturn(false);

			assertThrows(AuthException.class, () -> authService.reissue("bad-refresh-token"));
			verify(jwtTokenUtils, never()).getIdFromJwt(any());
		}

		@Test
		@DisplayName("저장된 Refresh Token이 없으면 예외")
		void reissue_no_saved_refresh_token() {
			String refreshTokenString = "refresh-token";

			when(jwtTokenUtils.isValidateToken(refreshTokenString)).thenReturn(true);
			when(jwtTokenUtils.getIdFromJwt(refreshTokenString)).thenReturn(9L);
			when(refreshTokenService.findRefreshToken(9L)).thenReturn(Optional.empty());

			assertThrows(AuthException.class, () -> authService.reissue(refreshTokenString));
		}

		@Test
		@DisplayName("저장된 Refresh Token과 불일치하면 예외")
		void reissue_mismatch() {
			String refreshTokenString = "refresh-token-x";

			when(jwtTokenUtils.isValidateToken(refreshTokenString)).thenReturn(true);
			when(jwtTokenUtils.getIdFromJwt(refreshTokenString)).thenReturn(11L);

			RefreshToken savedRefreshToken = mock(RefreshToken.class);
			when(refreshTokenService.findRefreshToken(11L)).thenReturn(Optional.of(savedRefreshToken));
			when(savedRefreshToken.isSame(refreshTokenString)).thenReturn(false);

			assertThrows(AuthException.class, () -> authService.reissue(refreshTokenString));
		}
	}

	@Nested
	@DisplayName("logOut")
	class Logout {

		@Test
		@DisplayName("저장된 Refresh Token이 있으면 삭제하고 디바이스도 제거")
		void logout_success() {
			Long userId = 77L;
			LogoutRequest request = mock(LogoutRequest.class);
			when(request.deviceId()).thenReturn("device-1");

			RefreshToken savedRefreshToken = mock(RefreshToken.class);
			when(refreshTokenService.findRefreshToken(userId)).thenReturn(Optional.of(savedRefreshToken));

			authService.logOut(userId, request);

			verify(refreshTokenService).deleteRefreshToken(77L);
			verify(userDeviceRepository).deleteByUserIdAndDeviceId(77L, "device-1");
		}

		@Test
		@DisplayName("저장된 Refresh Token이 없으면 예외")
		void logout_no_saved_token() {
			Long userId = 88L;
			LogoutRequest request = mock(LogoutRequest.class);

			when(refreshTokenService.findRefreshToken(userId)).thenReturn(Optional.empty());

			assertThrows(AuthException.class, () -> authService.logOut(userId, request));

			verify(refreshTokenService, never()).deleteRefreshToken(anyLong());
			verify(userDeviceRepository, never()).deleteByUserIdAndDeviceId(anyLong(), any());
		}
	}

	@Nested
	@DisplayName("unlinkOAuth")
	class Unlink {

		@Test
		@DisplayName("KAKAO: 사용자 정보로 적절한 전략을 구해 unlink 호출")
		void unlink_kakao_calls_strategy() {
			User user = mock(User.class);
			when(user.getOAuthType()).thenReturn(OAuthType.KAKAO);
			when(user.getProviderId()).thenReturn("pid-kakao");
			when(user.getOAuthRefreshToken()).thenReturn("prov-rt-kakao");

			when(strategyFactory.getStrategy("KAKAO")).thenReturn(oAuthStrategy);

			authService.unlinkOAuth(user);

			ArgumentCaptor<OAuthUnlinkRequest> captor = ArgumentCaptor.forClass(OAuthUnlinkRequest.class);
			verify(oAuthStrategy).unlink(captor.capture());

			OAuthUnlinkRequest sentRequest = captor.getValue();
			assertThat(sentRequest.providerId()).isEqualTo("pid-kakao");
			assertThat(sentRequest.refreshToken()).isEqualTo("prov-rt-kakao");
		}

		@Test
		@DisplayName("APPLE: 사용자 정보로 적절한 전략을 구해 unlink 호출")
		void unlink_apple_calls_strategy() {
			User user = mock(User.class);
			when(user.getOAuthType()).thenReturn(OAuthType.APPLE);
			when(user.getProviderId()).thenReturn("pid-apple");
			when(user.getOAuthRefreshToken()).thenReturn("prov-rt-apple");

			when(strategyFactory.getStrategy("APPLE")).thenReturn(oAuthStrategy);

			authService.unlinkOAuth(user);

			verify(oAuthStrategy).unlink(any(OAuthUnlinkRequest.class));
		}
	}
}
