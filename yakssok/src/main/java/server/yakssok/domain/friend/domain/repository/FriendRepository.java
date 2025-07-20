package server.yakssok.domain.friend.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import server.yakssok.domain.friend.domain.entity.Friend;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>, FriendQueryRepository {
	int countByUserId(Long userId);
}
