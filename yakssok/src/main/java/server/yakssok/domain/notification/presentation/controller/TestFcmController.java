package server.yakssok.domain.notification.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.application.service.PushService;
import server.yakssok.global.common.reponse.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/fcm")
@Tag(name = "Notification", description = "fcm 테스트 API")
public class TestFcmController {

	private final PushService pushService;

	@Operation(summary = "fcm 테스트용 알림 푸시 (data only)")
	@PostMapping
	public ApiResponse testSendData(
		@RequestBody TestSendDataRequest request
	) {
		pushService.sendSendData(request);
		return ApiResponse.success();
	}
}
