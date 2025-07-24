package server.yakssok.global.dummy;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.yakssok.domain.auth.application.service.RefreshTokenService;
import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.entity.UserDevice;
import server.yakssok.domain.user.domain.repository.UserDeviceRepository;
import server.yakssok.domain.user.domain.repository.UserRepository;
import server.yakssok.global.common.jwt.JwtTokenUtils;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class UserInitializer implements ApplicationRunner {

	static final String NICKNAME_RIA = "리아";
	static final String PROVIDER_ID_RIA = "lea1234";
	static final OAuthType OAUTH_TYPE_RIA = OAuthType.APPLE;

	static final String NICKNAME_INWOO = "인우";
	static final String PROVIDER_ID_INWOO = "inwoo1234";
	static final OAuthType OAUTH_TYPE_INWOO = OAuthType.KAKAO;

	private final UserRepository userRepository;
	private final JwtTokenUtils jwtTokenUtils;
	private final RefreshTokenService refreshTokenService;
	private final UserDeviceRepository userDeviceRepository;

	@Override
	public void run(ApplicationArguments args) {
		createIfNotExists(NICKNAME_RIA, PROVIDER_ID_RIA, OAUTH_TYPE_RIA);
		createIfNotExists(NICKNAME_INWOO, PROVIDER_ID_INWOO, OAUTH_TYPE_INWOO);
	}

	private void createIfNotExists(String nickName, String providerId, OAuthType oauthType) {
		if (!userRepository.existsUserByProviderId(oauthType, providerId)) {
			User user = User.create(nickName, null, oauthType, providerId);
			UserDevice userDevice = UserDevice.createUserDevice(user, "default-device-id", null, false);

			userRepository.save(user);
			userDeviceRepository.save(userDevice);

			String refreshToken = jwtTokenUtils.generateRefreshToken(user.getId());
			refreshTokenService.registerRefreshToken(user, refreshToken);
			log.info(">>> 계정 생성: nickName={}, providerId={}", nickName, providerId);
		} else {
			log.info(">>> 이미 존재: nickName={}, providerId={}", nickName, providerId);
		}
	}
}
