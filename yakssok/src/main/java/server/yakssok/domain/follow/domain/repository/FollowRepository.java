package server.yakssok.domain.follow.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import server.yakssok.domain.follow.domain.entity.Follow;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
	boolean existsByFollowerIdAndFollowingId(Long userId, Long followingId);

	List<Follow> findAllByFollowerId(Long userId);
}
