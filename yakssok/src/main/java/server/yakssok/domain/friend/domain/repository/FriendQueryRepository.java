package server.yakssok.domain.friend.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import server.yakssok.domain.friend.domain.entity.Friend;

public interface FriendQueryRepository {
	boolean isAlreadyFollow(Long userId, Long followingId);
	List<Friend> findMyFollowings(Long userId);
	List<Friend> findMyFollowers(Long userId);
	Optional<Friend> findByUserIdAndFollowingId(Long userId, Long followingId);
	List<Long> findPraiseCandidatesToday(Long userId, LocalDate today);
}