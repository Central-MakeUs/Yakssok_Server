package server.yakssok.domain.notification.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.application.service.NotificationService;
import server.yakssok.domain.notification.presentation.dto.request.MedicationNotificationRequest;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.security.YakssokUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Tag(name = "Notification", description = "알림 API")
public class NotificationController {
	private final NotificationService notificationService;

	@Operation(summary = "알림 저장")
	@PostMapping
	public ApiResponse createNotification(
		@AuthenticationPrincipal YakssokUserDetails userDetails,
		MedicationNotificationRequest medicationNotificationRequest
	) {
		Long userId = userDetails.getUserId();
		notificationService.createNotification(userId, medicationNotificationRequest);
		return ApiResponse.success();
	}
}
