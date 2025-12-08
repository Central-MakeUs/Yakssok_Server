package server.yakssok.domain.medication_schedule.batch.job;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.medication_schedule.domain.policy.OverduePolicy;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleAlarmDto;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.notification.application.service.PushService;
import server.yakssok.domain.notification.presentation.dto.NotificationDTO;
import server.yakssok.domain.user.domain.entity.User;

@Component
@RequiredArgsConstructor
public class NotTakenReportAlarmJob {

	private final PushService pushService;
	private final MedicationScheduleRepository medicationScheduleRepository;
	private final FriendRepository friendRepository;
	private final OverduePolicy overduePolicy;

	/**
	 * - 오늘 9시 이전까지 안 먹은 약이 하나라도 있는 유저의 메이트에게
	 *   유저당 1번씩 고발 알림을 전송한다.
	 */
	@Transactional
	public void sendNotTakenReportMedicationAlarms() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime delayBoundary = overduePolicy.delayBoundary(now);
		List<MedicationScheduleAlarmDto> notTakenSchedules =
			medicationScheduleRepository.findNotTakenSchedules(delayBoundary);
		List<NotTakenReportTarget> targets = createTargets(notTakenSchedules);
		targets.forEach(this::notifyFriends);
	}

	/**
	 * "미복약 스케줄 리스트" → "고발 알림 타겟 리스트" 로 변환
	 * - 유저당 대표 스케줄 1개만 사용
	 * - 각 유저를 팔로우하는 친구 목록까지 포함해 둔다.
	 */
	private List<NotTakenReportTarget> createTargets(List<MedicationScheduleAlarmDto> notTakenSchedules) {
		Map<Long, List<MedicationScheduleAlarmDto>> schedulesByUser =
			notTakenSchedules.stream()
				.collect(Collectors.groupingBy(MedicationScheduleAlarmDto::userId));

		return schedulesByUser.entrySet().stream()
			.map(entry -> {
				Long userId = entry.getKey();
				List<MedicationScheduleAlarmDto> userSchedules = entry.getValue();

				//대표 스케줄 1개만 사용
				MedicationScheduleAlarmDto representative = userSchedules.get(0);

				List<Friend> friends = friendRepository.findMyFollowers(userId);
				String followingNickName = representative.userNickName();

				return new NotTakenReportTarget(representative, friends, followingNickName);
			})
			.toList();
	}

	/**
	 * 한 유저에 대한 고발 알림을, 그 유저를 팔로우하는 친구들에게 전송
	 */
	private void notifyFriends(NotTakenReportTarget target) {
		for (Friend friend : target.friends()) {
			User receiver = friend.getUser();
			NotificationDTO friendNotificationDTO =
				NotificationDTO.fromMedicationScheduleForFriend(
					target.representativeSchedule(),
					receiver.getId(),
					target.followingNickName()
				);
			pushService.sendNotification(friendNotificationDTO);
		}
	}

	/**
	 * "고발 알림 타겟" 도메인 객체
	 * - representativeSchedule : 유저의 미복약 스케줄 중 대표 1개
	 * - friends               : 이 유저를 팔로우하는 친구들
	 * - followingNickName     : 메이트에게 보여줄 유저의 닉네임
	 */
	private record NotTakenReportTarget(
		MedicationScheduleAlarmDto representativeSchedule,
		List<Friend> friends,
		String followingNickName
	) {
	}
}
