package server.yakssok.domain.friend.presentation.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.applcation.service.FriendService;
import server.yakssok.domain.friend.presentation.dto.request.FollowFriendRequest;
import server.yakssok.domain.friend.presentation.dto.response.FollowerInfoGroupResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingInfoGroupResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusDetailResponse;
import server.yakssok.domain.friend.presentation.dto.response.FollowingMedicationStatusGroupResponse;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.security.YakssokUserDetails;
import server.yakssok.global.common.swagger.ApiErrorResponse;
import server.yakssok.global.common.swagger.ApiErrorResponses;
import server.yakssok.global.exception.ErrorCode;

@Tag(name = "Friend", description = "지인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {

	private final FriendService friendService;


	@Operation(summary = "지인 팔로우")
	@ApiErrorResponses(value = {
		@ApiErrorResponse(ErrorCode.ALREADY_FRIEND),
		@ApiErrorResponse(ErrorCode.INVALID_INVITE_CODE),
	})
	@PostMapping
	public ApiResponse followByInviteCode(
		@RequestBody FollowFriendRequest followRequest,
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		friendService.followFriendByInviteCode(userId, followRequest);
		return ApiResponse.success();
	}

	@Operation(summary = "나의 팔로잉 목록 조회")
	@GetMapping("/followings")
	public ApiResponse<FollowingInfoGroupResponse> findMyFollowings(
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		FollowingInfoGroupResponse friends = friendService.findMyFollowings(userId);
		return ApiResponse.success(friends);
	}

	@Operation(summary = "나의 팔로워 목록 조회")
	@GetMapping("/followers")
	public ApiResponse<FollowerInfoGroupResponse> findMyFollowers(
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		FollowerInfoGroupResponse friends = friendService.findMyFollowers(userId);
		return ApiResponse.success(friends);
	}

	@Operation(summary = "오늘 칭찬/잔소리 대상 지인 목록 조회")
	@GetMapping("/medication-status")
	public ApiResponse<FollowingMedicationStatusGroupResponse> getFollowingRemainingMedication(
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		return ApiResponse.success(friendService.getFollowingRemainingMedication(userId));
	}

	@Operation(summary = "오늘 지인 안먹은 약 상세 조회")
	@GetMapping("/friends/{friendId}/medication-status")
	public ApiResponse<FollowingMedicationStatusDetailResponse> getFollowingRemainingMedicationDetail(
		@AuthenticationPrincipal YakssokUserDetails userDetails,
		@PathVariable Long friendId
	) {
		Long userId = userDetails.getUserId();
		return ApiResponse.success(friendService.getFollowingRemainingMedicationDetail(userId, friendId));
	}
}