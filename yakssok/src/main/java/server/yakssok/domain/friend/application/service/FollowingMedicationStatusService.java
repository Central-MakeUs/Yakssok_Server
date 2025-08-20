package server.yakssok.domain.friend.application.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.feedback.domain.repository.FeedbackRepository;
import server.yakssok.domain.friend.application.service.mapper.MedicationStatusMapper;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusGroupResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusResponse;
import server.yakssok.domain.medication_schedule.application.service.MissedMedicationCalculator;
import server.yakssok.domain.medication_schedule.application.service.MedicationScheduleFinder;
import server.yakssok.domain.medication_schedule.domain.policy.OverduePolicy;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

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
			Map<Long, List<MedicationScheduleDto>> praise = findPraiseFollowing(userId, followingIds, today);
			return buildGroupResponse(friends, Map.of(), praise);
		}

		Map<Long, List<MedicationScheduleDto>> nag = findReportFollowing(userId, followingIds, delayBoundary, today);
		Map<Long, List<MedicationScheduleDto>> praise = findPraiseFollowing(userId, followingIds, today);
		return buildGroupResponse(friends, nag, praise);
	}

	private List<Long> extractFollowingIds(List<Friend> friends) {
		return friends.stream()
			.map(f -> f.getFollowing().getId())
			.toList();
	}

	private boolean isBeforeDailyGraceWindow(LocalDateTime now) {
		return overduePolicy.isBeforeDailyGraceWindow(now);
	}

	private Map<Long, List<MedicationScheduleDto>> findPraiseFollowing(
		Long userId,
		List<Long> followingIds,
		LocalDate today
	) {
		List<Long> alreadyPraisedFollowingIds = feedbackRepository.findPraisedUserIdsOnDate(userId, followingIds, today);
		List<Long> toPraiseFollowingIds = followingIds.stream()
			.filter(id -> !alreadyPraisedFollowingIds.contains(id))
			.toList();

		List<MedicationScheduleDto> allTakenToday = medicationScheduleFinder.findTodayAllTakenSchedules(
			toPraiseFollowingIds, today);
		return allTakenToday.stream()
			.collect(Collectors.groupingBy(
				MedicationScheduleDto::userId,
				Collectors.toList()
			));
	}

	private Map<Long, List<MedicationScheduleDto>> findReportFollowing(
		Long userId,
		List<Long> followingIds,
		LocalDateTime delayBoundary,
		LocalDate today
	) {
		Map<Long, LocalDateTime> lastNag =
			feedbackRepository.findTodayLastNagTimeToFollowings(userId, followingIds, today);
		List<MedicationScheduleDto> missedToday =
			medicationScheduleFinder.fetchTodayMissedMedications(followingIds, delayBoundary);
		return missedMedicationCalculator.missedAfterLastNagSchedules(missedToday, lastNag, today, overduePolicy);
	}

	/* ===================== helpers: 응답 조립 ===================== */
	private FollowingMedicationStatusGroupResponse buildGroupResponse(
		List<Friend> friends,
		Map<Long, List<MedicationScheduleDto>> nag,
		Map<Long, List<MedicationScheduleDto>> praise) {
		List<FollowingMedicationStatusResponse> list = medicationStatusMapper.toMedicationStatusResponses(friends, nag, praise);
		medicationStatusMapper.sortByNotTakenCount(list);
		return FollowingMedicationStatusGroupResponse.of(list);
	}
}
