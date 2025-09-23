package server.yakssok.domain.friend.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.friend.presentation.dto.request.FollowFriendRequestV2;
import server.yakssok.domain.friend.presentation.dto.response.FollowFriendResponse;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;

@Service
@RequiredArgsConstructor
public class FriendServiceV2 {

	private final UserService userService;
	private final RelationshipService relationshipService;
	private final FriendRepository friendRepository;

	@Transactional
	public FollowFriendResponse followFriendByInviteCode(Long userId, FollowFriendRequestV2 followFriendRequest) {
		String inviteCode = followFriendRequest.inviteCode();
		User following = userService.getUserIdByInviteCode(inviteCode);
		User user = userService.getActiveUser(userId);
		relationshipService.validateCanFollow(user.getId(), following.getId());
		followEachOther(followFriendRequest, user, following);
		return FollowFriendResponse.of(following);
	}

	private void followEachOther(FollowFriendRequestV2 followFriendRequest, User user, User following) {
		Friend userToFollowing = followFriendRequest.toFriend(user, following);
		Friend followingToUser = followFriendRequest.toFriend(following, user);
		friendRepository.saveAll(List.of(userToFollowing, followingToUser));
	}
}

