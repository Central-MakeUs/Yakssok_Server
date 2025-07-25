package server.yakssok.domain.medication_schedule.batch.job;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleAlarmDto;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.notification.application.service.NotificationService;
import server.yakssok.domain.notification.presentation.dto.NotificationRequest;

@Component
@RequiredArgsConstructor
public class MedicationAlarmJob {
	private final NotificationService notificationService;
	private final MedicationScheduleRepository medicationScheduleRepository;
	private final FriendRepository friendRepository;

	public void run() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime threshold = now.minusMinutes(30);
		List<MedicationScheduleAlarmDto> notTakenSchedules = medicationScheduleRepository
			.findNotTakenSchedules(threshold);
		for (MedicationScheduleAlarmDto schedule : notTakenSchedules) {
			notificationService.sendNotification(
				NotificationRequest.fromMedicationSchedule(schedule)
			);

			List<Friend> friends = friendRepository.findMyFollowers(schedule.userId());
			for (Friend friend : friends) {
				NotificationRequest friendRequest =
					NotificationRequest.fromScheduleForFriend(schedule, friend);
				notificationService.sendNotification(friendRequest);
			}
		}
	}
}
