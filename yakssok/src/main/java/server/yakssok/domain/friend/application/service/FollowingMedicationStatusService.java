package server.yakssok.domain.friend.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusDetailResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusGroupResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusResponse;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;
import server.yakssok.domain.medication_schedule.domain.repository.dto.RemainingMedicationDto;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class FollowingMedicationStatusService {
	private static final long OVERDUE_GRACE_MINUTES = 30L;

	private final FriendRepository friendRepository;
	private final FeedbackRepository feedbackRepository;
	private final MedicationScheduleRepository medicationScheduleRepository;
	private final MedicationStatusMapper medicationStatusMapper;

	@Transactional(readOnly = true)
	public FollowingMedicationStatusGroupResponse list(Long userId) {
		final LocalDateTime now = LocalDateTime.now();
		final LocalDate today = now.toLocalDate();
		final LocalDateTime delayBoundaryTime = now.minusMinutes(OVERDUE_GRACE_MINUTES);

		List<Friend> friends = friendRepository.findMyFollowings(userId);
		List<Long> followingIds = extractFollowingIds(friends);
		if (followingIds.isEmpty()) {
			return FollowingMedicationStatusGroupResponse.of(List.of());
		}

		// [자정 경계] 00:30 이전엔 오늘 기준 ‘30분 이상 지연’이 없음
		if (delayBoundaryTime.toLocalDate().isBefore(today)) {
			Set<Long> praiseCandidates = fetchPraiseCandidates(userId, today);
			List<FollowingMedicationStatusResponse> statusList =
				medicationStatusMapper.toMedicationStatusResponses(friends, Map.of(), praiseCandidates);
			medicationStatusMapper.sortByNotTakenCount(statusList);
			return FollowingMedicationStatusGroupResponse.of(statusList);
		}

		Map<Long, LocalDateTime> lastNagTime = fetchLastNagByFollowing(userId, followingIds, today);
		List<RemainingMedicationDto> missedToday = fetchTodayMissedMedications(followingIds, delayBoundaryTime);
		Map<Long, Integer> notTakenMap = countMissedAfterLastNag(missedToday, lastNagTime, today);
		Set<Long> praiseCandidates = fetchPraiseCandidates(userId, today);

		List<FollowingMedicationStatusResponse> statusList =
			medicationStatusMapper.toMedicationStatusResponses(friends, notTakenMap, praiseCandidates);
		medicationStatusMapper.sortByNotTakenCount(statusList);
		return FollowingMedicationStatusGroupResponse.of(statusList);
	}

	@Transactional(readOnly = true)
	public FollowingMedicationStatusDetailResponse detail(Long userId, Long followingId) {
		final LocalDateTime now = LocalDateTime.now();
		final LocalDate today = now.toLocalDate();
		final LocalDateTime delayBoundaryTime = now.minusMinutes(OVERDUE_GRACE_MINUTES);
		Friend friend = findFriend(userId, followingId);

		// [자정 경계] 00:30 이전엔 오늘 기준 ‘30분 이상 지연’이 없음
		if (delayBoundaryTime.toLocalDate().isBefore(today)) {
			return FollowingMedicationStatusDetailResponse.of(
				friend.getFollowing().getNickName(),
				friend.getRelationName(),
				List.of()
			);
		}
		LocalDateTime lastNagTime = getTodayLastNagTimeToFollowing(userId, followingId, today);
		LocalDateTime nagBoundary = (lastNagTime != null) ? lastNagTime : today.atStartOfDay();
		List<MedicationScheduleDto> schedules = medicationScheduleRepository
			.findRemainingMedicationDetail(followingId, delayBoundaryTime)
			.stream()
			.filter(ms -> {
				LocalTime graceEndTime =
					ms.intakeTime().plusMinutes(OVERDUE_GRACE_MINUTES);
				return graceEndTime.isAfter(nagBoundary.toLocalTime());
			})
			.toList();

		return FollowingMedicationStatusDetailResponse.of(
			friend.getFollowing().getNickName(),
			friend.getRelationName(),
			schedules
		);
	}

	private LocalDateTime getTodayLastNagTimeToFollowing(Long userId, Long followingId, LocalDate today) {
		return feedbackRepository.findTodayLastNagTimeToFollowing(userId, followingId, today);
	}

	private Set<Long> fetchPraiseCandidates(Long userId, LocalDate today) {
		return new HashSet<>(friendRepository.findPraiseCandidatesToday(userId, today));
	}

	private List<RemainingMedicationDto> fetchTodayMissedMedications(List<Long> followingIds, LocalDateTime delayBoundaryTime) {
		return medicationScheduleRepository.findTodayRemainingMedications(followingIds, delayBoundaryTime);
	}

	private Map<Long, LocalDateTime> fetchLastNagByFollowing(Long userId, List<Long> followingIds, LocalDate today) {
		return feedbackRepository.findTodayLastNagTimeToFollowings(userId, followingIds, today);
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
			if (scheduledDateTime.plusMinutes(OVERDUE_GRACE_MINUTES).isAfter(cutoff)) {
				map.merge(userId, 1, Integer::sum);
			}
		}
		return map;
	}

	public Friend findFriend(Long userId, Long followingId) {
		return friendRepository.findByUserIdAndFollowingId(userId, followingId)
			.orElseThrow(()-> new FriendException(ErrorCode.NOT_FRIEND));
	}

}
