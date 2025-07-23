package server.yakssok.domain.notification.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.presentation.dto.CreateFcmRequest;
import server.yakssok.domain.notification.application.service.FcmTokenService;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.security.YakssokUserDetails;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification", description = "알림 API")
public class FcmTokenController {
	private final FcmTokenService fcmTokenService;

	@PostMapping("/fcm-token")
	public ApiResponse saveFcmToken(
		@RequestBody CreateFcmRequest createFcmRequest,
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		fcmTokenService.saveFcmToken(userId, createFcmRequest);
		return ApiResponse.success();
	}
}
