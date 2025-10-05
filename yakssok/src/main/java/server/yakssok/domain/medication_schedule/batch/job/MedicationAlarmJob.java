package server.yakssok.domain.medication_schedule.batch.job;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
import server.yakssok.global.infra.rabbitmq.properties.MedicationQueueProperties;
import server.yakssok.domain.user.domain.entity.User;

@Component
@RequiredArgsConstructor
public class MedicationAlarmJob {
	private final PushService pushService;
	private final MedicationScheduleRepository medicationScheduleRepository;
	private final FriendRepository friendRepository;
	private final RabbitTemplate rabbitTemplate;
	private final MedicationQueueProperties medicationQueueProperties;
	private final OverduePolicy overduePolicy;

	@Transactional
	public void sendNotTakenReportMedicationAlarms() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime delayBoundary = overduePolicy.delayBoundary(now);
		List<MedicationScheduleAlarmDto> notTakenSchedules =
			medicationScheduleRepository.findNotTakenSchedules(delayBoundary);

		for (MedicationScheduleAlarmDto schedule : notTakenSchedules) {
			if (overduePolicy.isBeforeDailyGraceWindow(now)) {
				continue;
			}
			List<Friend> friends = friendRepository.findMyFollowers(schedule.userId());
			String followingNickName = schedule.userNickName();
			sendMedicationReportAlarms(schedule, friends, followingNickName);
		}
	}

	private void sendMedicationReportAlarms(MedicationScheduleAlarmDto schedule, List<Friend> friends, String followingNickName) {
		for (Friend friend : friends) {
			User receiver = friend.getUser();
			NotificationDTO friendNotificationDTO =
				NotificationDTO.fromMedicationScheduleForFriend(schedule, receiver.getId(), followingNickName);
			pushService.sendNotification(friendNotificationDTO);
		}
	}

	@Transactional
	public void sendMedicationAlarms() {
		LocalDateTime now = LocalDateTime.now();
		List<MedicationScheduleAlarmDto> scheduledMedications
			= medicationScheduleRepository.findSchedules(now);
		for (MedicationScheduleAlarmDto schedule : scheduledMedications) {
			NotificationDTO notificationDTO = NotificationDTO.fromMedicationSchedule(schedule);
			sendToMedicationQueue(notificationDTO);
		}
	}

	private void sendToMedicationQueue(NotificationDTO notificationDTO) {
		String exchange = medicationQueueProperties.exchange();
		String routingKey = medicationQueueProperties.routingKey();
		rabbitTemplate.convertAndSend(exchange, routingKey, notificationDTO);
	}
}
