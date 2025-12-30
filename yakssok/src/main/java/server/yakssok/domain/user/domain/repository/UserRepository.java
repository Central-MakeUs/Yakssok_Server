package server.yakssok.domain.user.domain.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import server.yakssok.domain.user.domain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserQueryRepository {
	Optional<User> findByInviteCodeValue(String inviteCode);
	Optional<User> findByIdAndIsDeletedFalse(Long userId);
	List<User> findAllByIsDeletedFalse();
}
