package server.yakssok.domain.user.repository;

import java.util.Optional;

import server.yakssok.domain.user.domain.entity.Provider;
import server.yakssok.domain.user.domain.entity.User;

public interface UserQueryRepository {
	Optional<User> findUserByProviderId(Provider provider, String providerId);
	boolean existsUserByProviderId(Provider provider, String providerId);

}
