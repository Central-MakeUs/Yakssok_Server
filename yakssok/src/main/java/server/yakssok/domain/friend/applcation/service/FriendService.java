package server.yakssok.domain.friend.applcation.service;

import java.util.ArrayList;
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
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusGroupResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusResponse;
import server.yakssok.domain.medication_schedule.application.service.MedicationScheduleService;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;

@Service
@RequiredArgsConstructor
public class FriendService {
	private final UserService userService;
	private final RelationshipService relationshipService;
	private final FriendRepository friendRepository;
	private final MedicationScheduleRepository medicationScheduleRepository;
	private final MedicationScheduleService medicationScheduleService;

	@Transactional
	public void followFriendByInviteCode(Long userId, FollowFriendRequest followFriendRequest) {
		String inviteCode = followFriendRequest.inviteCode();
		User following = userService.getUserIdByInviteCode(inviteCode);
		User user = userService.getUserByUserId(userId);
		relationshipService.validateCanFollow(user.getId(), following.getId());
		Friend friend = followFriendRequest.toFriend(user, following);
		friendRepository.save(friend);
	}

	@Transactional
	public FollowingInfoGroupResponse findMyFollowings(Long userId) {
		List<Friend> friends = friendRepository.findMyFollowings(userId);
		List<FollowingInfoResponse> friendInfoResponses = friends.stream()
			.map(friend -> FollowingInfoResponse.from(friend)).toList();
		return FollowingInfoGroupResponse.of(friendInfoResponses);
	}

	@Transactional
	public FollowerInfoGroupResponse findMyFollowers(Long userId) {
		List<Friend> friends = friendRepository.findMyFollowers(userId);
		List<FollowerInfoResponse> followerInfoResponses = friends.stream()
			.map(friend -> FollowerInfoResponse.from(friend)).toList();
		return FollowerInfoGroupResponse.of(followerInfoResponses);
	}

	//TODO : 1+2n개 쿼리 개선
	@Transactional(readOnly = true)
	public FollowingMedicationStatusGroupResponse getFollowingMedicationStatuses(Long userId) {
		List<Friend> followingList = friendRepository.findMyFollowings(userId);

		List<FollowingMedicationStatusResponse> medicationStatuses = new ArrayList<>();
		for (Friend friend : followingList) {
			Long followingId = friend.getFollowing().getId();
			if (!medicationScheduleService.isExistTodaySchedule(followingId)) {
				continue;
			}
			int remainingCount = medicationScheduleRepository.countRemainingMedicationsForToday(followingId);
			medicationStatuses.add(FollowingMedicationStatusResponse.of(friend, remainingCount));
		}
		return FollowingMedicationStatusGroupResponse.of(medicationStatuses);
	}
}

