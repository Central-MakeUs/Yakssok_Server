package server.yakssok.domain.notification.presentation.dto;


import lombok.AccessLevel;
import lombok.Builder;
import server.yakssok.domain.feedback.domain.entity.Feedback;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleAlarmDto;
import server.yakssok.domain.notification.application.constants.NotificationBodyConstants;
import server.yakssok.domain.notification.application.util.NotificationTitleUtils;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.entity.NotificationType;

@Builder(access = AccessLevel.PRIVATE)
public record NotificationDTO(
	Long senderId,
	Long receiverId,
	Long scheduleId,
	String title,
	String body,
	NotificationType type,
	String soundType
) {

	public static NotificationDTO fromNotTakenMedicationSchedule(MedicationScheduleAlarmDto schedule) {
		return NotificationDTO.builder()
			.receiverId(schedule.userId())
			.scheduleId(schedule.scheduleId())
			.title(NotificationTitleUtils.createMedicationReminderTitle(schedule.userNickName(), schedule.medicineName()))
			.body(NotificationBodyConstants.MEDICATION_NOT_TAKEN_BODY)
			.type(NotificationType.MEDICATION_NOT_TAKEN)
			.soundType(schedule.soundType().name())
			.build();
	}

	public static NotificationDTO fromMutualFollowFeedback(
		Long senderId,
		Long receiverId,
		String receiverName,
		String relationName,
		Feedback feedback
	) {
		return NotificationDTO.builder()
			.senderId(senderId)
			.receiverId(receiverId)
			.title(NotificationTitleUtils.createFeedbackTitleMutual(feedback.getFeedbackType(), receiverName, relationName))
			.body(feedback.getMessage())
			.type(feedback.getFeedbackType().toNotificationType())
			.build();
	}

	public static NotificationDTO fromOneWayFollowFeedback(
		Long senderId,
		String senderName,
		Long receiverId,
		Feedback feedback
	) {
		return NotificationDTO.builder()
			.senderId(senderId)
			.receiverId(receiverId)
			.title(NotificationTitleUtils.createFeedbackTitleOneWay(feedback.getFeedbackType(), senderName))
			.body(feedback.getMessage())
			.type(feedback.getFeedbackType().toNotificationType())
			.build();
	}


	public static NotificationDTO fromMedicationScheduleForFriend(
		MedicationScheduleAlarmDto schedule,
		Long receiverId,
		String followingNickName
	) {
		return NotificationDTO.builder()
			.receiverId(receiverId)
			.scheduleId(schedule.scheduleId())
			.title(NotificationTitleUtils.createFriendNotTakenAlarmTitle(followingNickName))
			.body(NotificationBodyConstants.MEDICATION_NOT_TAKEN_BODY_FOR_FRIEND)
			.type(NotificationType.MEDICATION_NOT_TAKEN_FOR_FRIEND)
			.build();
	}

	public static NotificationDTO fromMedicationSchedule(MedicationScheduleAlarmDto schedule) {
		return NotificationDTO.builder()
			.receiverId(schedule.userId())
			.scheduleId(schedule.scheduleId())
			.title(NotificationTitleUtils.createMedicationTitle(schedule.medicineName()))
			.body(NotificationBodyConstants.MEDICATION_TAKE_BODY)
			.type(NotificationType.MEDICATION_TAKE)
			.soundType(schedule.soundType().name())
			.build();
	}

	public Notification toNotification() {
		return Notification.createNotification(
			senderId,
			receiverId,
			scheduleId,
			title,
			body,
			type
		);
	}
}
