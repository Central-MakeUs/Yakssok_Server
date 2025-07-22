package server.yakssok.global.dummy;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class FriendInitializer implements ApplicationRunner {

	private final UserRepository userRepository;
	private final FriendRepository friendRepository;

	@Override
	public void run(ApplicationArguments args) {
		User inwoo = userRepository.findUserByProviderId(UserInitializer.OAUTH_TYPE_INWOO, UserInitializer.PROVIDER_ID_INWOO).orElseThrow();
		User ria = userRepository.findUserByProviderId(UserInitializer.OAUTH_TYPE_RIA, UserInitializer.PROVIDER_ID_RIA).orElseThrow();
		if (!friendRepository.isAlreadyFollow(inwoo.getId(), ria.getId())) {
			Friend inwooFriend = Friend.create(inwoo, ria, "울첫째딸");
			friendRepository.save(inwooFriend);
			log.info(">>> 인우 → 리아 지인 관계 생성");
		} else {
			log.info(">>> 인우 → 리아 지인 관계 이미 존재");
		}
		if (!friendRepository.isAlreadyFollow(ria.getId(), inwoo.getId())) {
			Friend leaFriend = Friend.create(ria, inwoo, "울아빠");
			friendRepository.save(leaFriend);
			log.info(">>> 리아 → 인우 지인 관계 생성");
		} else {
			log.info(">>> 리아 → 인우 지인 관계 이미 존재");
		}
	}
}
