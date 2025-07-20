package server.yakssok.domain.friend.domain.repository;

import java.util.List;

import server.yakssok.domain.friend.domain.entity.Friend;

public interface FriendRepositoryCustom {
	boolean isAlreadyFollow(Long userId, Long friendId);
	List<Friend> findFollowingsByUserId(Long userId);

}