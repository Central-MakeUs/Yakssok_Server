package server.yakssok.domain.friend.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.feedback.domain.repository.FeedbackRepository;
import server.yakssok.domain.friend.application.exception.FriendException;
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
import server.yakssok.domain.medication_schedule.domain.repository.RemainingMedicationDto;
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
	private final FeedbackRepository feedbackRepository;
	private final MedicationScheduleRepository medicationScheduleRepository;

	@Transactional
	public void followFriendByInviteCode(Long userId, FollowFriendRequest followFriendRequest) {
		String inviteCode = followFriendRequest.inviteCode();
		User following = userService.getUserIdByInviteCode(inviteCode);
		User user = userService.getActiveUser(userId);
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

		// --- 잔소리: 마지막 잔소리 시각 이후에 생긴 미복용만 카운트 ---
		Map<Long, LocalDateTime> lastNagDateTime = feedbackRepository.findTodayLastNagTime(userId, followingIds, LocalDate.now());
		List<RemainingMedicationDto> notTakenMedications = getNagUserRemainingMedications(followingIds);
		Map<Long, Integer> notTakenMap = buildAdjustedNotTakenMap(notTakenMedications, lastNagDateTime, LocalDate.now());

		// --- 칭찬: 오늘 전부 복용
		List<Long> praisedToday = feedbackRepository.findPraisedToday(userId, followingIds, LocalDate.now());
		List<Long> fullyTakenIds = getPraiseUser(followingIds);

		List<FollowingMedicationStatusResponse> statusList = toMedicationStatusResponses(
			friends, notTakenMap, fullyTakenIds, praisedToday);

		sortByNotTakenCount(statusList);
		return FollowingMedicationStatusGroupResponse.of(statusList);
	}

	private Map<Long, Integer> buildAdjustedNotTakenMap(List<RemainingMedicationDto> notTakenMedications,
		Map<Long, LocalDateTime> lastNagDateTime, LocalDate now) {
		Map<Long, Integer> map = new HashMap<>();
		for (RemainingMedicationDto ms : notTakenMedications) {
			Long uid = ms.userId();
			LocalDateTime cutoff = lastNagDateTime.getOrDefault(uid, now.atStartOfDay());
			if (ms.scheduledTime().isAfter(cutoff.toLocalTime().minusMinutes(30))) {
				map.merge(uid, 1, Integer::sum);
			}
		}
		return map;
	}

	private List<Long> getPraiseUser(List<Long> followingIds) {
		List<Long> fullyTakenIds = medicationScheduleRepository
			.findUserIdsWithAllTakenToday(followingIds, LocalDate.now());
		return fullyTakenIds;
	}

	private List<RemainingMedicationDto> getNagUserRemainingMedications(List<Long> followingIds) {
		return medicationScheduleRepository
			.findTodayRemainingMedications(followingIds, LocalDateTime.now());
	}

	private void sortByNotTakenCount(List<FollowingMedicationStatusResponse> statusList) {
		statusList.sort(Comparator.comparingInt(FollowingMedicationStatusResponse::notTakenCount).reversed());
	}

	private List<FollowingMedicationStatusResponse> toMedicationStatusResponses(List<Friend> friends,
		Map<Long, Integer> notTakenMap, List<Long> fullyTakenIds, List<Long> praisedToday) {
		List<FollowingMedicationStatusResponse> result = new ArrayList<>();
		for (Friend friend : friends) {
			Long followingId = friend.getFollowing().getId();
			int notTaken = notTakenMap.getOrDefault(followingId, 0);

			if (notTaken > 0) {
				result.add(FollowingMedicationStatusResponse.of(friend, notTaken));
			} else if (fullyTakenIds.contains(followingId) && !praisedToday.contains(followingId)) {
				result.add(FollowingMedicationStatusResponse.of(friend, 0));
			}
		}
		return result;
	}

	@Transactional(readOnly = true)
	public FollowingMedicationStatusDetailResponse getFollowingRemainingMedicationDetail(Long userId, Long followingId) {
		Friend friend = findFriend(userId, followingId);
		List<MedicationScheduleDto> schedules = medicationScheduleRepository
			.findRemainingMedicationDetail(followingId, LocalDateTime.now());

		return FollowingMedicationStatusDetailResponse.of(
			friend.getFollowing().getNickName(),
			friend.getRelationName(),
			schedules
		);
	}

	public Friend findFriend(Long userId, Long followingId) {
		return friendRepository.findByUserIdAndFollowingId(userId, followingId)
			.orElseThrow(()-> new FriendException(ErrorCode.NOT_FRIEND));
	}
}

