package server.yakssok.domain.friend.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import server.yakssok.domain.medication_schedule.domain.repository.dto.RemainingMedicationDto;
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

	@Transactional(readOnly = true)
	public FollowingInfoGroupResponse findMyFollowings(Long userId) {
		List<Friend> friends = friendRepository.findMyFollowings(userId);
		List<FollowingInfoResponse> friendInfoResponses = friends.stream()
			.map(friend -> FollowingInfoResponse.from(friend)).toList();
		return FollowingInfoGroupResponse.of(friendInfoResponses);
	}

	@Transactional(readOnly = true)
	public FollowerInfoGroupResponse findMyFollowers(Long userId) {
		List<Friend> friends = friendRepository.findMyFollowers(userId);
		List<FollowerInfoResponse> followerInfoResponses = friends.stream()
			.map(friend -> FollowerInfoResponse.from(friend)).toList();
		return FollowerInfoGroupResponse.of(followerInfoResponses);
	}

	@Transactional(readOnly = true)
	public FollowingMedicationStatusGroupResponse getFollowingRemainingMedication(Long userId) {
		final LocalDate today = LocalDate.now();
		final LocalDateTime now = LocalDateTime.now();
		List<Friend> friends = friendRepository.findMyFollowings(userId);
		List<Long> followingIds = extractFollowingIds(friends);
		if (followingIds.isEmpty()) {
			return FollowingMedicationStatusGroupResponse.of(List.of());
		}

		Map<Long, LocalDateTime> lastNagAt = fetchLastNagByFollowing(userId, followingIds, today);
		List<RemainingMedicationDto> missedToday = fetchTodayMissedMedications(followingIds, now);
		Map<Long, Integer> notTakenMap = countMissedAfterLastNag(missedToday, lastNagAt, today);
		Set<Long> praiseCandidates = fetchPraiseCandidates(userId, today);

		List<FollowingMedicationStatusResponse> statusList =
			toMedicationStatusResponses(friends, notTakenMap, praiseCandidates);
		sortByNotTakenCount(statusList);
		return FollowingMedicationStatusGroupResponse.of(statusList);
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

	private Set<Long> fetchPraiseCandidates(Long userId, LocalDate today) {
		return new HashSet<>(friendRepository.findPraiseCandidatesToday(userId, today));
	}

	private List<RemainingMedicationDto> fetchTodayMissedMedications(List<Long> followingIds, LocalDateTime now) {
		return medicationScheduleRepository.findTodayRemainingMedications(followingIds, now);
	}

	private Map<Long, LocalDateTime> fetchLastNagByFollowing(Long userId, List<Long> followingIds, LocalDate today) {
		return feedbackRepository.findTodayLastNagTime(userId, followingIds, today);
	}

	private static List<Long> extractFollowingIds(List<Friend> friends) {
		return friends.stream()
			.map(f -> f.getFollowing().getId())
			.toList();
	}

	private Map<Long, Integer> countMissedAfterLastNag(
		List<RemainingMedicationDto> notTakenMedications,
		Map<Long, LocalDateTime> lastNagDateTime, LocalDate today
	) {

		Map<Long, Integer> map = new HashMap<>();
		LocalDateTime startOfDay = today.atStartOfDay();

		for (RemainingMedicationDto ms : notTakenMedications) {
			Long userId = ms.userId();
			LocalDateTime scheduledDateTime = LocalDateTime.of(ms.scheduledDate(), ms.scheduledTime());
			LocalDateTime cutoff = lastNagDateTime.getOrDefault(userId, startOfDay);
			if (scheduledDateTime.isAfter(cutoff)) {
				map.merge(userId, 1, Integer::sum);
			}
		}
		return map;
	}


	private void sortByNotTakenCount(List<FollowingMedicationStatusResponse> statusList) {
		statusList.sort(Comparator.comparingInt(FollowingMedicationStatusResponse::notTakenCount).reversed());
	}

	private List<FollowingMedicationStatusResponse> toMedicationStatusResponses(
		List<Friend> friends,
		Map<Long, Integer> notTakenMap,
		Set<Long> praisedToday
	) {
		List<FollowingMedicationStatusResponse> result = new ArrayList<>();
		for (Friend friend : friends) {
			Long followingId = friend.getFollowing().getId();
			int notTaken = notTakenMap.getOrDefault(followingId, 0);

			if (notTaken > 0) {
				result.add(FollowingMedicationStatusResponse.of(friend, notTaken));
			} else if (praisedToday.contains(followingId)) {
				result.add(FollowingMedicationStatusResponse.of(friend, 0));
			}
		}
		return result;
	}

	public Friend findFriend(Long userId, Long followingId) {
		return friendRepository.findByUserIdAndFollowingId(userId, followingId)
			.orElseThrow(()-> new FriendException(ErrorCode.NOT_FRIEND));
	}
}

