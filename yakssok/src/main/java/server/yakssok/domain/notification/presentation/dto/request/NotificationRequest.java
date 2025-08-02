package server.yakssok.domain.notification.presentation.dto.request;


import lombok.AccessLevel;
import lombok.Builder;
import server.yakssok.domain.feeback.domain.entity.Feedback;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleAlarmDto;
import server.yakssok.domain.notification.application.service.constants.NotificationBodyConstants;
import server.yakssok.domain.notification.application.service.NotificationTitleUtils;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.entity.NotificationType;
import server.yakssok.domain.user.domain.entity.User;

@Builder(access = AccessLevel.PRIVATE)
public record NotificationRequest(
	Long senderId,
	Long receiverId,
	Long scheduleId,
	String title,
	String body,
	NotificationType type
) {

	public static NotificationRequest fromMedicationSchedule(MedicationScheduleAlarmDto schedule) {
		return NotificationRequest.builder()
			.receiverId(schedule.userId())
			.scheduleId(schedule.scheduleId())
			.title(NotificationTitleUtils.createMedicationReminderTitle(schedule.userNickName(), schedule.medicineName()))
			.body(NotificationBodyConstants.MEDICATION_NOT_TAKEN_BODY)
			.type(NotificationType.MEDICATION_NOT_TAKEN)
			.build();
	}

	public static NotificationRequest fromFeedback(
		Long senderId,
		String senderName,
		Long receiverId,
		String relationName,
		Feedback feedback
	) {
		return NotificationRequest.builder()
			.senderId(senderId)
			.receiverId(receiverId)
			.title(NotificationTitleUtils.createFeedbackTitle(feedback.getFeedbackType(), senderName, relationName))
			.body(feedback.getMessage())
			.type(feedback.getFeedbackType().toNotificationType())
			.build();
	}

	public static NotificationRequest fromScheduleForFriend(
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
