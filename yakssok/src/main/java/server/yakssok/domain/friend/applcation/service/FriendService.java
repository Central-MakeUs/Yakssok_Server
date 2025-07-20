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

		Long friendId = userService.getUserIdByInviteCode(inviteCode);
		validateCanFollow(userId, friendId);
		createFriend(userId, friendId, relationName);
	}

	private void createFriend(Long userId, Long friendId, String relationName) {
		Friend friend = Friend.create(userId, friendId, relationName);
		friendRepository.save(friend);
	}

	private void validateCanFollow(Long userId, Long friendId) {
		validateSelfFollow(userId, friendId);
		validateAlreadyFriend(userId, friendId);
	}

	private static void validateSelfFollow(Long userId, Long friendId) {
		boolean isSelf = Objects.equals(userId, friendId);
		if (isSelf) {
			throw new FriendException(ErrorCode.CANNOT_FOLLOW_SELF);
		}
	}

	private void validateAlreadyFriend(Long userId, Long friendId) {
		boolean isExists = friendRepository.isAlreadyFriend(userId, friendId);
		if (isExists) {
			throw new FriendException(ErrorCode.ALREADY_FRIEND);
		}
	}

	@Transactional
	public FriendInfoGroupResponse findMyFriends(Long userId) {
		List<Friend> friendsList = friendRepository.findAllByUserId(userId);
		List<FriendInfoResponse> friendInfoResponses = friendsList.stream()
			.map(friend -> {
				Long friendId = friend.getFriendId();
				String relationName = friend.getRelationName();
				String profileImageUrl = userService.findUserProfileByUserId(friendId);
				return new FriendInfoResponse(friendId, relationName, profileImageUrl);
			}).toList();
		return FriendInfoGroupResponse.of(friendInfoResponses);
	}

	public boolean isFollowing(Long userId, Long friendId) {
		return friendRepository.isAlreadyFriend(userId, friendId);
	}
}

