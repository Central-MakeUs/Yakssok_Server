package server.yakssok.domain.friend.applcation.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.applcation.exception.FriendException;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.friend.presentation.dto.request.FollowFriendRequest;
import server.yakssok.domain.friend.presentation.dto.response.FriendInfoGroupResponse;
import server.yakssok.domain.friend.presentation.dto.response.FriendInfoResponse;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class FriendService {
	private final UserService userService;
	private final FriendRepository friendRepository;

	@Transactional
	public void followFriendByInviteCode(Long userId, FollowFriendRequest followFriendRequest) {
		String inviteCode = followFriendRequest.inviteCode();
		User following = userService.getUserIdByInviteCode(inviteCode);
		User user = userService.getUserByUserId(userId);
		validateCanFollow(user, following);
		Friend friend = followFriendRequest.createFriend(user, following);
		friendRepository.save(friend);
	}

	private void validateCanFollow(User user, User following) {
		Long userId = user.getId();
		Long followingId = following.getId();
		validateSelfFollow(userId, followingId);
		validateAlreadyFollow(userId, followingId);
	}

	private static void validateSelfFollow(Long userId, Long followingId) {
		boolean isSelf = Objects.equals(userId, followingId);
		if (isSelf) {
			throw new FriendException(ErrorCode.CANNOT_FOLLOW_SELF);
		}
	}

	private void validateAlreadyFollow(Long userId, Long followingId) {
		boolean isExists = friendRepository.isAlreadyFollow(userId, followingId);
		if (isExists) {
			throw new FriendException(ErrorCode.ALREADY_FRIEND);
		}
	}

	@Transactional
	public FriendInfoGroupResponse findMyFollowings(Long userId) {
		List<Friend> friends = friendRepository.findFollowingsByUserId(userId);
		List<FriendInfoResponse> friendInfoResponses = friends.stream()
			.map(friend -> {
				return FriendInfoResponse.of(friend);
			}).toList();
		return FriendInfoGroupResponse.of(friendInfoResponses);
	}

	public boolean isFollowing(Long userId, Long followingId) {
		return friendRepository.isAlreadyFollow(userId, followingId);
	}
}

