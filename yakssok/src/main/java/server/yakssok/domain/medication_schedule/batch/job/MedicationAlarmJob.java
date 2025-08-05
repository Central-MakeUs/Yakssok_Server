package server.yakssok.domain.medication_schedule.batch.job;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleAlarmDto;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.notification.application.service.PushService;
import server.yakssok.global.infra.rabbitmq.MedicationQueueProperties;
import server.yakssok.domain.notification.presentation.dto.request.NotificationRequest;
import server.yakssok.domain.user.domain.entity.User;

@Component
@RequiredArgsConstructor
public class MedicationAlarmJob {
	private final PushService pushService;
	private final MedicationScheduleRepository medicationScheduleRepository;
	private final FriendRepository friendRepository;
	private static final int NOT_TAKEN_MINUTES_LIMIT = 30;
	private final RabbitTemplate rabbitTemplate;
	private final MedicationQueueProperties medicationQueueProperties;

	@Transactional
	public void sendNotTakenMedicationAlarms() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime notTakenLimitTime = now.minusMinutes(NOT_TAKEN_MINUTES_LIMIT);
		List<MedicationScheduleAlarmDto> notTakenSchedules = medicationScheduleRepository
			.findNotTakenSchedules(notTakenLimitTime);
		for (MedicationScheduleAlarmDto schedule : notTakenSchedules) {
			pushService.sendData(
				NotificationRequest.fromNotTakenMedicationSchedule(schedule)
			);

			List<Friend> friends = friendRepository.findMyFollowers(schedule.userId());
			String followingNickName = schedule.userNickName();
			for (Friend friend : friends) {
				User receiver = friend.getUser();
				NotificationRequest friendRequest =
					NotificationRequest.fromMedicationScheduleForFriend(schedule, receiver.getId(), followingNickName);
				pushService.sendNotification(friendRequest);
			}
		}
	}

	@Transactional
	public void sendMedicationAlarms() {
		LocalDateTime now = LocalDateTime.now();
		List<MedicationScheduleAlarmDto> scheduledMedications
			= medicationScheduleRepository.findSchedules(now);
		for (MedicationScheduleAlarmDto schedule : scheduledMedications) {
			NotificationRequest request = NotificationRequest.fromMedicationSchedule(schedule);
			sendToMedicationQueue(request);
		}
	}

	private void sendToMedicationQueue(NotificationRequest request) {
		String exchange = medicationQueueProperties.exchange();
		String routingKey = medicationQueueProperties.routingKey();
		rabbitTemplate.convertAndSend(exchange, routingKey, request);
	}
}
