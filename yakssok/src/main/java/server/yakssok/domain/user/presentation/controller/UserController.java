package server.yakssok.domain.user.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.presentation.dto.response.FindUserInfoResponse;
import server.yakssok.domain.user.presentation.dto.request.UpdateUserInfoRequest;
import server.yakssok.domain.user.presentation.dto.response.FindMyInfoResponse;
import server.yakssok.domain.user.presentation.dto.response.FindUserInviteCodeResponse;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.security.YakssokUserDetails;
import server.yakssok.global.common.swagger.ApiErrorResponse;
import server.yakssok.global.exception.ErrorCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "유저 API")
public class UserController {
	private final UserService userService;

	@Operation(summary = "내 정보 조회")
	@ApiErrorResponse(ErrorCode.NOT_FOUND_USER)
	@GetMapping("/me")
	public ApiResponse<FindMyInfoResponse> findMyInfo(
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		return ApiResponse.success(userService.findMyInfo(userId));
	}

	@Operation(summary = "유저 정보 수정(프로필, 닉네임)")
	@ApiErrorResponse(ErrorCode.NOT_FOUND_USER)
	@PutMapping("/me")
	public ApiResponse updateUserInfo(
		@Valid @RequestBody UpdateUserInfoRequest userInfoRequest,
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		userService.updateUserInfo(userId, userInfoRequest);
		return ApiResponse.success();
	}

	@Operation(summary = "초대 코드 조회")
	@ApiErrorResponse(ErrorCode.NOT_FOUND_USER)
	@GetMapping("/invite-code")
	public ApiResponse<FindUserInviteCodeResponse> findUserInviteCode(
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		return ApiResponse.success(userService.findUserInviteCode(userId));
	}

	@Operation(summary = "초대 코드로 유저 정보 조회")
	@ApiErrorResponse(ErrorCode.NOT_FOUND_USER)
	@ApiErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR)
	@GetMapping
	public ApiResponse<FindUserInfoResponse> findUserInfoByInviteCode(
		@Parameter(description = "초대 코드", required = true, example = "JoWRYS3Fz")
		@RequestParam String inviteCode
	) {
		return ApiResponse.success(userService.findUserInfoByInviteCode(inviteCode));
	}
}
