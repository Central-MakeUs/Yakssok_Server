package server.yakssok.domain.user.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.presentation.dto.request.UpdateUserInfoRequest;
import server.yakssok.domain.user.presentation.dto.response.FindUserInfoResponse;
import server.yakssok.global.ApiResponse;
import server.yakssok.global.common.swagger.ApiErrorResponse;
import server.yakssok.global.exception.ErrorCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "유저 API")
public class UserController {
	private final UserService userService;

	@Operation(summary = "유저 정보 조회(프로필, 닉네임)")
	@ApiErrorResponse(ErrorCode.NOT_FOUND_USER)
	@GetMapping("/me")
	public ApiResponse<FindUserInfoResponse> findUserInfo(
		@AuthenticationPrincipal UserDetails userDetails
	) {
		Long userId = Long.valueOf(userDetails.getUsername());
		return ApiResponse.success(userService.findUserInfo(userId));
	}

	@Operation(summary = "유저 정보 수정(프로필, 닉네임)")
	@ApiErrorResponse(ErrorCode.NOT_FOUND_USER)
	@PutMapping("/me")
	public ApiResponse updateUserInfo(
		@Valid @RequestBody UpdateUserInfoRequest userInfoRequest,
		@AuthenticationPrincipal UserDetails userDetails
	) {
		Long userId = Long.valueOf(userDetails.getUsername());
		userService.updateUserInfo(userId, userInfoRequest);
		return ApiResponse.success();
	}
}
