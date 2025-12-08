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

	@Transactional
	public void sendNotTakenReportMedicationAlarms() {
		LocalDateTime now = LocalDateTime.now();
		// 정책상 9시 이후에만 동작하고 싶다면, 루프 밖에서 한 번만 체크
		if (overduePolicy.isBeforeDailyGraceWindow(now)) {
			return;
		}
		// 1) "오늘 9시 이전" 을 기준으로 지연 경계 계산
		LocalDateTime delayBoundary = overduePolicy.delayBoundary(now);

		// 2) 오늘 9시 이전까지 복약해야 했는데 아직 안 먹은 스케줄 전체 조회
		List<MedicationScheduleAlarmDto> notTakenSchedules =
			medicationScheduleRepository.findNotTakenSchedules(delayBoundary);

		// 3) 유저별로 "안 먹은 약이 하나라도 있는지"만 보면 되므로, userId 기준으로 그룹핑
		Map<Long, List<MedicationScheduleAlarmDto>> schedulesByUser =
			notTakenSchedules.stream()
				.collect(Collectors.groupingBy(MedicationScheduleAlarmDto::userId));

		// 4) 유저당 1번만 고발 알림 전송
		for (Map.Entry<Long, List<MedicationScheduleAlarmDto>> entry : schedulesByUser.entrySet()) {
			Long userId = entry.getKey();
			List<MedicationScheduleAlarmDto> userSchedules = entry.getValue();

			// 어차피 "안 먹은 약이 있다"는 사실만 필요하니 대표 하나만 사용
			MedicationScheduleAlarmDto representative = userSchedules.get(0);
			List<Friend> friends = friendRepository.findMyFollowers(userId);
			String followingNickName = representative.userNickName();
			notifyFriends(representative, friends, followingNickName);
		}
	}

	/**
	 * 한 유저에 대한 고발 알림을, 그 유저를 팔로우하는 친구들에게 전송
	 */
	private void notifyFriends(MedicationScheduleAlarmDto schedule, List<Friend> friends, String followingNickName) {
		for (Friend friend : friends) {
			User receiver = friend.getUser();
			NotificationDTO friendNotificationDTO =
				NotificationDTO.fromMedicationScheduleForFriend(schedule, receiver.getId(), followingNickName);
			pushService.sendNotification(friendNotificationDTO);
		}
	}
}
