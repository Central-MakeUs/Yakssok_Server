package server.yakssok.domain.notification.presentation.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import server.yakssok.domain.feedback.domain.entity.Feedback;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleAlarmDto;
import server.yakssok.domain.notification.application.constants.FeedbackTemplates;
import server.yakssok.domain.notification.application.constants.MedicationTakenTemplates;
import server.yakssok.domain.notification.application.constants.NotTakenReminderTemplates;
import server.yakssok.domain.notification.application.constants.NotTakenReportTemplates;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.entity.NotificationType;

@Builder(access = AccessLevel.PRIVATE)
public record NotificationDTO(
	Long senderId,
	@NotNull
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
			.title(NotTakenReminderTemplates.randomTitle(schedule.medicineName()))
			.body(NotTakenReminderTemplates.randomBody())
			.type(NotificationType.MEDICATION_NOT_TAKEN)
			.soundType(schedule.soundType().name())
			.build();
	}

	public static NotificationDTO fromFeedback(
		Long senderId,
		String senderName,
		Long receiverId,
		Feedback feedback
	) {
		return NotificationDTO.builder()
			.senderId(senderId)
			.receiverId(receiverId)
			.title(FeedbackTemplates.getTitle(feedback.getFeedbackType(), senderName))
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
			.title(NotTakenReportTemplates.getTitle(followingNickName))
			.body(NotTakenReportTemplates.getBody())
			.type(NotificationType.MEDICATION_NOT_TAKEN_FOR_FRIEND)
			.build();
	}

	public static NotificationDTO fromMedicationSchedule(MedicationScheduleAlarmDto schedule) {
		return NotificationDTO.builder()
			.receiverId(schedule.userId())
			.scheduleId(schedule.scheduleId())
			.title(MedicationTakenTemplates.getTitle(schedule.medicineName()))
			.body(MedicationTakenTemplates.getBody())
			.type(NotificationType.MEDICATION_TAKE)
			.soundType(schedule.soundType().name())
			.build();
	}

	public static NotificationDTO fromNotice(Long userId, String title, String body) {
		return NotificationDTO.builder()
			.receiverId(userId)
			.title(title)
			.body(body)
			.type(NotificationType.NOTICE)
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
