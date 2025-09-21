package server.yakssok.domain.friend.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.application.service.FriendService;
import server.yakssok.domain.friend.presentation.dto.request.FollowFriendRequest;
import server.yakssok.domain.friend.presentation.dto.response.FollowFriendResponse;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.security.YakssokUserDetails;
import server.yakssok.global.common.swagger.ApiErrorResponse;
import server.yakssok.global.common.swagger.ApiErrorResponses;
import server.yakssok.global.exception.ErrorCode;

@Tag(name = "Friend", description = "친구 API V2")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/friends")
public class FriendControllerV2 {
	private final FriendService friendService;

	@Operation(summary = "지인 팔로우")
	@ApiErrorResponses(value = {
		@ApiErrorResponse(ErrorCode.ALREADY_FRIEND),
		@ApiErrorResponse(ErrorCode.INVALID_INVITE_CODE),
	})
	@PostMapping
	public ApiResponse<FollowFriendResponse> followByInviteCode(
		@RequestBody @Valid FollowFriendRequest followRequest,
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		FollowFriendResponse followFriendResponse = friendService.followFriendByInviteCodeV2(userId, followRequest);
		return ApiResponse.success(followFriendResponse);
	}

}
