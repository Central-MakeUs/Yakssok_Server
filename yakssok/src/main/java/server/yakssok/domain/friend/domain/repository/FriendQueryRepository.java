package server.yakssok.domain.friend.domain.repository;

import java.util.List;

import server.yakssok.domain.friend.domain.entity.Friend;

public interface FriendQueryRepository {
	boolean isAlreadyFollow(Long userId, Long followingId);
	List<Friend> findMyFollowings(Long userId);
	List<Friend> findMyFollowers(Long userId);
}