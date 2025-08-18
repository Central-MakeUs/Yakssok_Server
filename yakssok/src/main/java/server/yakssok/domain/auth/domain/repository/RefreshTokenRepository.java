package server.yakssok.domain.auth.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import server.yakssok.domain.auth.domain.entity.RefreshToken;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByUserId(Long userId);
	@Modifying
	@Query("DELETE FROM RefreshToken r WHERE r.user.id = :userId")
	void deleteAllByUserId(Long userId);
}
