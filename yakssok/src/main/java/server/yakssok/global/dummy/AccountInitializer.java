package server.yakssok.global.dummy;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.yakssok.domain.auth.application.service.RefreshTokenService;
import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.repository.UserRepository;
import server.yakssok.global.common.jwt.JwtTokenUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountInitializer implements ApplicationRunner {

	private final UserRepository userRepository;
	private final JwtTokenUtils jwtTokenUtils;
	private final RefreshTokenService refreshTokenService;

	@Override
	public void run(ApplicationArguments args) {
		createIfNotExists("리아", "lea1234");
		createIfNotExists("인우", "inwoo1234");
	}

	private void createIfNotExists(String nickName, String providerId) {
		if (!userRepository.existsUserByProviderId(OAuthType.KAKAO, providerId)) {
			User user = User.create(
				nickName,
				null,
				"kakao",
				providerId,
				false,
				null
			);
			userRepository.save(user);
			String refreshToken = jwtTokenUtils.generateRefreshToken(user.getId());
			refreshTokenService.registerRefreshToken(user, refreshToken);
			log.info(">>> 계정 생성: nickName={}, providerId={}", nickName, providerId);
		} else {
			log.info(">>> 이미 존재: nickName={}, providerId={}", nickName, providerId);
		}
	}
}
