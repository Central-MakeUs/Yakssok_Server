package server.yakssok.domain.friend.applcation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.friend.presentation.dto.request.FollowFriendRequest;
import server.yakssok.domain.friend.presentation.dto.response.FollowerInfoGroupResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowerInfoResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingInfoGroupResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingInfoResponse;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;

@Service
@RequiredArgsConstructor
public class FriendService {
	private final UserService userService;
	private final RelationshipService relationshipService;
	private final FriendRepository friendRepository;

	@Transactional
	public void followFriendByInviteCode(Long userId, FollowFriendRequest followFriendRequest) {
		String inviteCode = followFriendRequest.inviteCode();
		User following = userService.getUserIdByInviteCode(inviteCode);
		User user = userService.getUserByUserId(userId);
		relationshipService.validateCanFollow(user.getId(), following.getId());
		Friend friend = followFriendRequest.createFriend(user, following);
		friendRepository.save(friend);
	}

	@Transactional
	public FollowingInfoGroupResponse findMyFollowings(Long userId) {
		List<Friend> friends = friendRepository.findMyFollowings(userId);
		List<FollowingInfoResponse> friendInfoResponses = friends.stream()
			.map(friend -> {
				return FollowingInfoResponse.of(friend);
			}).toList();
		return FollowingInfoGroupResponse.of(friendInfoResponses);
	}

	@Transactional
	public FollowerInfoGroupResponse findMyFollowers(Long userId) {
		List<Friend> friends = friendRepository.findMyFollowers(userId);
		List<FollowerInfoResponse> followerInfoResponses = friends.stream()
			.map(friend -> {
				return FollowerInfoResponse.of(friend);
			}).toList();
		return FollowerInfoGroupResponse.of(followerInfoResponses);
	}
}

