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
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class FriendService {
	private final UserService userService;
	private final FriendRepository friendRepository;

	@Transactional
	public void followFriendByInviteCode(Long userId, FollowFriendRequest followFriendRequest) {
		String inviteCode = followFriendRequest.inviteCode();
		String relationName = followFriendRequest.relationName();

		Long followingId = userService.getUserIdByInviteCode(inviteCode);
		validateCanFollow(userId, followingId);
		Friend friend = followFriendRequest.createFriend(userId, followingId);
		friendRepository.save(friend);
	}

	private void validateCanFollow(Long userId, Long friendId) {
		validateSelfFollow(userId, friendId);
		validateAlreadyFollow(userId, friendId);
	}

	private static void validateSelfFollow(Long userId, Long friendId) {
		boolean isSelf = Objects.equals(userId, friendId);
		if (isSelf) {
			throw new FriendException(ErrorCode.CANNOT_FOLLOW_SELF);
		}
	}

	private void validateAlreadyFollow(Long userId, Long friendId) {
		boolean isExists = friendRepository.isAlreadyFollow(userId, friendId);
		if (isExists) {
			throw new FriendException(ErrorCode.ALREADY_FRIEND);
		}
	}

	@Transactional
	public FriendInfoGroupResponse findMyFollowings(Long userId) {
		List<Friend> followings = friendRepository.findFollowingsByUserId(userId);
		List<FriendInfoResponse> friendInfoResponses = followings.stream()
			.map(following -> {
				Long followingId = following.getFriendId();
				String relationName = following.getRelationName();
				String profileImageUrl = userService.findUserProfileByUserId(followingId);
				return new FriendInfoResponse(followingId, relationName, profileImageUrl);
			}).toList();
		return FriendInfoGroupResponse.of(friendInfoResponses);
	}

	public boolean isFollowing(Long userId, Long friendId) {
		return friendRepository.isAlreadyFollow(userId, friendId);
	}
}

