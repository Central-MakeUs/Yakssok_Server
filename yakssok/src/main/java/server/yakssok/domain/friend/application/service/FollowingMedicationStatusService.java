package server.yakssok.domain.friend.application.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.feedback.domain.repository.FeedbackRepository;
import server.yakssok.domain.friend.application.exception.FriendException;
import server.yakssok.domain.friend.application.service.mapper.MedicationStatusMapper;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusDetailResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusGroupResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusResponse;
import server.yakssok.domain.medication_schedule.application.service.MissedMedicationCalculator;
import server.yakssok.domain.medication_schedule.application.service.MedicationScheduleFinder;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;
import server.yakssok.domain.medication_schedule.domain.policy.OverduePolicy;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class FollowingMedicationStatusService {

	private final FriendRepository friendRepository;
	private final FeedbackRepository feedbackRepository;
	private final MedicationStatusMapper medicationStatusMapper;
	private final MedicationScheduleFinder medicationScheduleFinder;
	private final OverduePolicy overduePolicy;
	private final MissedMedicationCalculator missedMedicationCalculator;

	/**
	 * [나의 팔로잉 중 잔소리/칭찬 대상 목록 조회]
	 *
	 * 규칙
	 * - 잔소리 대상: 오늘 복용해야 할 약 중 ‘(예: 30분) 이상 지연’된 약이 1개 이상
	 *   단, "오늘 00:30 이전"에는 오늘 기준 지연이 존재하지 않는 것으로 간주 (자정 경계).
	 * - 칭찬 대상: 오늘 복용해야 할 약을 "모두 복용"한 경우
	 *
	 * 주의
	 * - ‘잔소리’는 "오늘 내 잔소리 이후에 발생한 지연만" 카운트해 중복 잔소리를 줄임.
	 * - ‘칭찬’은 오늘 내가 칭찬했다면 제외
	 */
	@Transactional(readOnly = true)
	public FollowingMedicationStatusGroupResponse getFollowingMedicationStatusGroup(Long userId) {
		LocalDateTime now = LocalDateTime.now();
		LocalDate today = now.toLocalDate();
		LocalDateTime delayBoundary = overduePolicy.delayBoundary(now);

		List<Friend> friends = friendRepository.findMyFollowings(userId);
		List<Long> followingIds = extractFollowingIds(friends);
		if (followingIds.isEmpty()) return FollowingMedicationStatusGroupResponse.of(List.of());

		if (isBeforeDailyGraceWindow(now)) {
			Set<Long> praise = findPraiseCandidates(userId, today);
			return buildGroupResponse(friends, Map.of(), praise);
		}

		Map<Long, Integer> notTaken = calculateNotTakenCounts(userId, followingIds, delayBoundary, today);
		Set<Long> praise = findPraiseCandidates(userId, today);
		return buildGroupResponse(friends, notTaken, praise);
	}

	/**
	 * [잔소리 대상 팔로잉의 안먹은 약 상세 조회]
	 *
	 * - 팔로잉의 오늘 복용해야 할 약 중 ‘30분 이상 지연’된 약의 상세 조회
	 * - "내가 오늘 마지막으로 잔소리한 시각" 이후의 지연만 보여줌
	 */
	@Transactional(readOnly = true)
	public FollowingMedicationStatusDetailResponse getFollowingMedicationStatusDetail(Long userId, Long followingId) {
		LocalDateTime now = LocalDateTime.now();
		LocalDate today = now.toLocalDate();
		Friend friend = findFriend(userId, followingId);

		if (isBeforeDailyGraceWindow(now)) {
			return emptyDetail(friend);
		}
		LocalDateTime delayBoundary = toDelayBoundary(now);
		LocalDateTime nagBoundary = calculateNagBoundary(userId, followingId, today);

		List<MedicationScheduleDto> raw =
			medicationScheduleFinder.findRemainingMedicationDetail(followingId, delayBoundary);
		List<MedicationScheduleDto> filtered =
			missedMedicationCalculator.filterOverdueAfterNag(raw, nagBoundary, overduePolicy);
		return buildDetailResponse(friend, filtered);
	}

	private static List<Long> extractFollowingIds(List<Friend> friends) {
		return friends.stream()
			.map(f -> f.getFollowing().getId())
			.toList();
	}

	private Friend findFriend(Long userId, Long followingId) {
		return friendRepository.findByUserIdAndFollowingId(userId, followingId)
			.orElseThrow(()-> new FriendException(ErrorCode.NOT_FRIEND));
	}

	private LocalDateTime toDelayBoundary(LocalDateTime now) {
		return overduePolicy.delayBoundary(now);
	}
	private boolean isBeforeDailyGraceWindow(LocalDateTime now) {
		return overduePolicy.isBeforeDailyGraceWindow(now);
	}

	private Set<Long> findPraiseCandidates(Long userId, LocalDate today) {
		return new HashSet<>(friendRepository.findPraiseCandidatesToday(userId, today));
	}

	private Map<Long, Integer> calculateNotTakenCounts(
		Long userId,
		List<Long> followingIds,
		LocalDateTime delayBoundary,
		LocalDate today
	) {
		List<MedicationSchedule> missedToday =
			medicationScheduleFinder.fetchTodayMissedMedications(followingIds, delayBoundary);
		Map<Long, LocalDateTime> lastNag =
			feedbackRepository.findTodayLastNagTimeToFollowings(userId, followingIds, today);
		return missedMedicationCalculator.countMissedAfterLastNag(missedToday, lastNag, today, overduePolicy);
	}

	private LocalDateTime calculateNagBoundary(Long userId, Long followingId, LocalDate today) {
		LocalDateTime lastNag = feedbackRepository.findTodayLastNagTimeToFollowing(userId, followingId, today);
		return (lastNag != null) ? lastNag : today.atStartOfDay();
	}

	/* ===================== helpers: 응답 조립 ===================== */

	private FollowingMedicationStatusGroupResponse buildGroupResponse(
		List<Friend> friends, Map<Long, Integer> notTaken, Set<Long> praise) {

		List<FollowingMedicationStatusResponse> list =
			medicationStatusMapper.toMedicationStatusResponses(friends, notTaken, praise);
		medicationStatusMapper.sortByNotTakenCount(list);
		return FollowingMedicationStatusGroupResponse.of(list);
	}

	private FollowingMedicationStatusDetailResponse emptyDetail(Friend friend) {
		return FollowingMedicationStatusDetailResponse.of(
			friend.getFollowing().getNickName(),
			friend.getRelationName(),
			List.of()
		);
	}

	private FollowingMedicationStatusDetailResponse buildDetailResponse(
		Friend friend, List<MedicationScheduleDto> filtered) {

		return FollowingMedicationStatusDetailResponse.of(
			friend.getFollowing().getNickName(),
			friend.getRelationName(),
			filtered
		);
	}
}
