package server.yakssok.domain.notification.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import server.yakssok.domain.notification.domain.entity.Notification;
import server.yakssok.domain.notification.domain.entity.NotificationType;

public record MedicationNotificationRequest(
	@Schema(description = "스케줄 ID", example = "1")
	Long scheduleId,
	@Schema(description = "알림 제목", example = "비타민 챙길 시간이에요!")
	String title,
	@Schema(description = "알림 바디", example = "약 먹고, 섭취 완료 처리해주세요!")
	String body
) {

	public Notification toNotification(Long receiverId, boolean isSuccess) {
		return Notification.createNotification(
			null,
			receiverId,
			scheduleId,
			title,
			body,
			NotificationType.MEDICATION_TAKE,
			isSuccess
		);
	}
}
