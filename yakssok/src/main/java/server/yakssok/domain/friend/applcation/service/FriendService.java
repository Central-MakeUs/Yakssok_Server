package server.yakssok.domain.friend.applcation.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.applcation.exception.FriendException;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.friend.presentation.dto.request.FollowFriendRequest;
import server.yakssok.domain.friend.presentation.dto.response.FollowerInfoGroupResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowerInfoResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingInfoGroupResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingInfoResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusDetailResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusGroupResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusResponse;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class FriendService {
	private final UserService userService;
	private final RelationshipService relationshipService;
	private final FriendRepository friendRepository;
	private final MedicationScheduleRepository medicationScheduleRepository;

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

	@Transactional(readOnly = true)
	public FollowingMedicationStatusGroupResponse getFollowingRemainingMedication(Long userId) {
		List<Friend> friends = friendRepository.findMyFollowings(userId);
		List<Long> followingIds = friends.stream()
			.map(f -> f.getFollowing().getId())
			.toList();

		// 오늘 스케줄 있는 팔로잉 id 추출
		List<Long> activeFollowingIds = medicationScheduleRepository
			.findFollowingIdsWithTodaySchedule(followingIds, LocalDate.now());

		// 남은 약 개수 map
		Map<Long, Integer> remainingMap = medicationScheduleRepository
			.countTodayRemainingMedications(activeFollowingIds, LocalDate.now());

		List<FollowingMedicationStatusResponse> statusList = friends.stream()
			.filter(f -> activeFollowingIds.contains(f.getFollowing().getId()))
			.map(f -> toMedicationStatusResponse(f, remainingMap))
			.sorted(Comparator.comparingInt(FollowingMedicationStatusResponse::notTakenCount).reversed())
			.toList();

		return FollowingMedicationStatusGroupResponse.of(statusList);
	}

	private FollowingMedicationStatusResponse toMedicationStatusResponse(Friend friend, Map<Long, Integer> remainingMap) {
		Long id = friend.getFollowing().getId();
		int cnt = remainingMap.getOrDefault(id, 0);
		return FollowingMedicationStatusResponse.of(friend, cnt);
	}


	@Transactional(readOnly = true)
	public FollowingMedicationStatusDetailResponse getFollowingRemainingMedicationDetail(Long userId, Long friendId) {
		Friend friend = friendRepository.findByUserIdAndFollowingId(userId, friendId)
			.orElseThrow(() -> new FriendException(ErrorCode.NOT_FRIEND));
		List<MedicationScheduleDto> schedules = medicationScheduleRepository
			.findRemainingMedicationDetail(friendId, LocalDate.now());

		return FollowingMedicationStatusDetailResponse.of(
			friend.getFollowing().getNickName(),
			friend.getRelationName(),
			schedules
		);
	}
}

