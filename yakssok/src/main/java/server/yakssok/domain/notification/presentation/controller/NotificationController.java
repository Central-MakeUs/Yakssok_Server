package server.yakssok.domain.notification.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.application.service.NotificationService;
import server.yakssok.domain.notification.presentation.dto.request.MedicationNotificationRequest;
import server.yakssok.domain.notification.presentation.dto.response.NotificationResponse;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.reponse.PageResponse;
import server.yakssok.global.common.security.YakssokUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Tag(name = "Notification", description = "알림 API")
public class NotificationController {
	private final NotificationService notificationService;

	@Operation(summary = "알림 목록 조회")
	@GetMapping
	public ApiResponse<PageResponse<NotificationResponse>> findMyNotifications(
		@Parameter(description = "이전 페이지 마지막 알림 ID (없으면 최신부터)")
		@RequestParam(required = false) Long lastId,
		@Parameter(description = "한 번에 가져올 알림 개수 (기본 20)")
		@RequestParam(required = false, defaultValue = "20") int limit,
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		return ApiResponse.success(notificationService.findMyNotifications(userId, lastId, limit));
	}
}
