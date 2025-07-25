package server.yakssok.domain.notification.presentation.dto;


import server.yakssok.domain.feeback.domain.entity.Feedback;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleAlarmDto;
import server.yakssok.domain.notification.application.service.NotificationBodyConstants;
import server.yakssok.domain.notification.application.service.NotificationTitleUtils;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.entity.NotificationType;

public record NotificationRequest(
	Long senderId,
	Long receiverId,
	Long scheduleId,
	String title,
	String body,
	NotificationType type
) {

	public static NotificationRequest fromMedicationSchedule(MedicationScheduleAlarmDto schedule) {
		return new NotificationRequest(
			null,
			schedule.userId(),
			schedule.scheduleId(),
			NotificationTitleUtils.createMedicationReminderTitle(schedule.userNickName(), schedule.medicineName()),
			NotificationBodyConstants.MEDICATION_NOT_TAKEN_BODY,
			NotificationType.MEDICATION_NOT_TAKEN);
	}

	public static NotificationRequest fromFeedback(Long senderId, String senderName, Long receiverId, Feedback feedback) {
		return new NotificationRequest(
			senderId,
			receiverId,
			null,
			NotificationTitleUtils.createFeedbackTitle(feedback.getFeedbackType(), senderName),
			feedback.getMessage(),
			NotificationType.FEEDBACK
		);
	}

	public Notification toNotification(boolean isSuccess) {
		return Notification.createNotification(
			senderId,
			receiverId,
			scheduleId,
			title,
			body,
			type,
			isSuccess
			);

	}
}
