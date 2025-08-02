package server.yakssok.domain.medication_schedule.batch.job;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleAlarmDto;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.notification.application.service.PushService;
import server.yakssok.domain.notification.presentation.dto.request.NotificationRequest;
import server.yakssok.domain.user.domain.entity.User;

@Component
@RequiredArgsConstructor
public class MedicationAlarmJob {
	private final PushService pushService;
	private final MedicationScheduleRepository medicationScheduleRepository;
	private final FriendRepository friendRepository;
	private static final int NOT_TAKEN_MINUTES_LIMIT = 30;

	public void run() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime notTakenLimitTime = now.minusMinutes(NOT_TAKEN_MINUTES_LIMIT);
		List<MedicationScheduleAlarmDto> notTakenSchedules = medicationScheduleRepository
			.findNotTakenSchedules(notTakenLimitTime);
		for (MedicationScheduleAlarmDto schedule : notTakenSchedules) {
			pushService.sendNotification(
				NotificationRequest.fromMedicationSchedule(schedule)
			);

			List<Friend> friends = friendRepository.findMyFollowers(schedule.userId());
			String followingNickName = schedule.userNickName();
			for (Friend friend : friends) {
				User receiver = friend.getUser();
				NotificationRequest friendRequest =
					NotificationRequest.fromScheduleForFriend(schedule, receiver.getId(), followingNickName);
				pushService.sendNotification(friendRequest);
			}
		}
	}
}
