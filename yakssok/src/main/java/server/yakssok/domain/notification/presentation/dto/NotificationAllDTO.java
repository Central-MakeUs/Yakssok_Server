package server.yakssok.domain.notification.presentation.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import server.yakssok.domain.feedback.domain.entity.Feedback;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleAlarmDto;
import server.yakssok.domain.notification.application.constants.NotificationBodyConstants;
import server.yakssok.domain.notification.application.util.NotificationTitleUtils;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.entity.NotificationType;

@Builder(access = AccessLevel.PRIVATE)
public record NotificationAllDTO(
	String title,
	String body,
	NotificationType type
) {

	public static NotificationAllDTO fromNotice(String title, String body) {
		return NotificationAllDTO.builder()
			.title(title)
			.body(body)
			.type(NotificationType.NOTICE)
			.build();
	}
}
