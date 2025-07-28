package server.yakssok.domain.friend.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import server.yakssok.domain.friend.domain.entity.Friend;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>, FriendQueryRepository {
	int countByUserId(Long userId);
	@Modifying
	@Query("DELETE FROM Friend f WHERE f.user.id = :userId")
	void deleteAllByFollowingId(Long userId);
	@Modifying
	@Query("DELETE FROM Friend f WHERE f.following.id = :userId")
	void deleteAllByUserId(Long userId);
}
