package server.yakssok.domain.friend.domain.repository;

public interface FriendRepositoryCustom {
	boolean isAlreadyFriend(Long userId, Long followingFriendId);
}