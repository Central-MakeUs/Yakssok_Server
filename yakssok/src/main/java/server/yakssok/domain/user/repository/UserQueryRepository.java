package server.yakssok.domain.user.repository;

import java.util.Optional;

import server.yakssok.domain.user.domain.entity.OAuthType;
import server.yakssok.domain.user.domain.entity.User;

public interface UserQueryRepository {
	Optional<User> findUserByProviderId(String oAuthType, String providerId);
	boolean existsUserByProviderId(OAuthType provider, String providerId);
}
