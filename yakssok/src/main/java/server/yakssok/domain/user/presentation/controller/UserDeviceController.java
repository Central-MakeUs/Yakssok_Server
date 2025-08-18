package server.yakssok.domain.user.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.user.application.service.UserDeviceService;
import server.yakssok.domain.user.presentation.dto.request.RegisterDeviceRequest;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.security.YakssokUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-devices")
@Tag(name = "UserDevice", description = "유저 디바이스 API")
public class UserDeviceController {
	private final UserDeviceService userDeviceService;

	@Operation(
		summary = "디바이스 등록 및 FCM 토큰 저장",
		description = "이전과 같은 디바이스 id를 보내면 덮어쓰기 됩니다. "
	)
	@PostMapping("/devices")
	public ApiResponse registerDevice(
		@RequestBody @Valid RegisterDeviceRequest request,
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		userDeviceService.registerOrUpdateDevice(userId, request);
		return ApiResponse.success();
	}
}
