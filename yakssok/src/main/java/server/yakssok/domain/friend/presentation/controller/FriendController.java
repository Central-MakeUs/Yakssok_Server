package server.yakssok.domain.friend.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.applcation.service.FriendService;
import server.yakssok.domain.friend.presentation.dto.request.FollowFriendRequest;
import server.yakssok.domain.friend.presentation.dto.response.FriendInfoGroupResponse;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.security.YakssokUserDetails;

@Tag(name = "Friend", description = "지인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendController {

	private final FriendService friendService;

	@Operation(summary = "지인 팔로우")
	@PostMapping
	public ApiResponse followByInviteCode(
		@RequestBody FollowFriendRequest followRequest,
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		friendService.followFriendByInviteCode(userDetails.getUserId(), followRequest);
		return ApiResponse.success();
	}

	@Operation(summary = "나의 팔로잉 목록 조회")
	@GetMapping
	public ApiResponse<FriendInfoGroupResponse> findMyFollowings(
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		FriendInfoGroupResponse friends = friendService.findMyFollowings(userDetails.getUserId());
		return ApiResponse.success(friends);
	}
}