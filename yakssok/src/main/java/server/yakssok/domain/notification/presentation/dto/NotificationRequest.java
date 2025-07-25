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
	String title,
	String body,
	NotificationType type
) {

	public static NotificationRequest fromMedicationSchedule(MedicationScheduleAlarmDto schedule) {
		return new NotificationRequest(
			null,
			schedule.userId(),
			NotificationTitleUtils.createMedicationReminderTitle(schedule.userNickName(), schedule.medicineName()),
			NotificationBodyConstants.MEDICATION_NOT_TAKEN_BODY,
			NotificationType.MEDICATION_NOT_TAKEN);
	}

	public Notification toNotification(boolean isSuccess) {
		return Notification.createNotification(
			senderId,
			receiverId,
			title,
			body,
			type,
			isSuccess
			);

	}

	public static NotificationRequest fromFeedback(Long senderId, String senderName, Long receiverId, Feedback feedback) {
		return new NotificationRequest(
			senderId,
			receiverId,
			NotificationTitleUtils.createFeedbackTitle(feedback.getFeedbackType(), senderName),
			feedback.getMessage(),
			NotificationType.FEEDBACK
		);
	}
}
