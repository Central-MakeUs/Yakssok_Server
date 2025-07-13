package server.yakssok.domain.follow.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.follow.applcation.service.FollowService;
import server.yakssok.domain.follow.presentation.dto.request.FollowRequest;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.security.YakssokUserDetails;

@Tag(name = "Follow", description = "지인 팔로우 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {

	private final FollowService friendService;

	@Operation(summary = "지인 팔로우")
	@PostMapping
	public ApiResponse followByInviteCode(
		@RequestBody FollowRequest followRequest,
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		friendService.followByInviteCode(userDetails.getUserId(), followRequest);
		return ApiResponse.success();
	}
}