package server.yakssok.domain.notification.presentation.dto.request;


import lombok.AccessLevel;
import lombok.Builder;
import server.yakssok.domain.feeback.domain.entity.Feedback;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleAlarmDto;
import server.yakssok.domain.notification.application.constants.NotificationBodyConstants;
import server.yakssok.domain.notification.application.util.NotificationTitleUtils;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.entity.NotificationType;

@Builder(access = AccessLevel.PRIVATE)
public record NotificationRequest(
	Long senderId,
	Long receiverId,
	Long scheduleId,
	String title,
	String body,
	NotificationType type,
	String soundType
) {

	public static NotificationRequest fromNotTakenMedicationSchedule(MedicationScheduleAlarmDto schedule) {
		return NotificationRequest.builder()
			.receiverId(schedule.userId())
			.scheduleId(schedule.scheduleId())
			.title(NotificationTitleUtils.createMedicationReminderTitle(schedule.userNickName(), schedule.medicineName()))
			.body(NotificationBodyConstants.MEDICATION_NOT_TAKEN_BODY)
			.type(NotificationType.MEDICATION_NOT_TAKEN)
			.soundType(schedule.soundType().name())
			.build();
	}

	public static NotificationRequest fromMutualFollowFeedback(
		Long senderId,
		Long receiverId,
		String receiverName,
		String relationName,
		Feedback feedback
	) {
		return NotificationRequest.builder()
			.senderId(senderId)
			.receiverId(receiverId)
			.title(NotificationTitleUtils.createFeedbackTitleMutual(feedback.getFeedbackType(), receiverName, relationName))
			.body(feedback.getMessage())
			.type(feedback.getFeedbackType().toNotificationType())
			.build();
	}

	public static NotificationRequest fromOneWayFollowFeedback(
		Long senderId,
		String senderName,
		Long receiverId,
		Feedback feedback
	) {
		return NotificationRequest.builder()
			.senderId(senderId)
			.receiverId(receiverId)
			.title(NotificationTitleUtils.createFeedbackTitleOneWay(feedback.getFeedbackType(), senderName))
			.body(feedback.getMessage())
			.type(feedback.getFeedbackType().toNotificationType())
			.build();
	}


	public static NotificationRequest fromMedicationScheduleForFriend(
		MedicationScheduleAlarmDto schedule,
		Long receiverId,
		String followingNickName
	) {
		return NotificationRequest.builder()
			.receiverId(receiverId)
			.scheduleId(schedule.scheduleId())
			.title(NotificationTitleUtils.createFriendNotTakenAlarmTitle(followingNickName))
			.body(NotificationBodyConstants.MEDICATION_NOT_TAKEN_BODY_FOR_FRIEND)
			.type(NotificationType.MEDICATION_NOT_TAKEN_FOR_FRIEND)
			.build();
	}

	public static NotificationRequest fromMedicationSchedule(MedicationScheduleAlarmDto schedule) {
		return NotificationRequest.builder()
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
