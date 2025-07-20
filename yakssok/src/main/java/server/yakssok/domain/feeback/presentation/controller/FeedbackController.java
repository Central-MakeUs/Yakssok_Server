package server.yakssok.domain.feeback.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.feeback.application.service.FeedbackService;
import server.yakssok.domain.feeback.presentation.dto.request.CreateFeedbackRequest;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.security.YakssokUserDetails;
import server.yakssok.global.common.swagger.ApiErrorResponse;
import server.yakssok.global.exception.ErrorCode;

@Tag(name = "Feedback", description = "피드백 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedbacks")
public class FeedbackController {
	private final FeedbackService feedbackService;

	@Operation(summary = "피드백(잔소리/칭찬) 보내기")
	@ApiErrorResponse(value = ErrorCode.INVALID_INPUT_VALUE)
	@PostMapping
	public ApiResponse sendFeedback(
		@RequestBody CreateFeedbackRequest request,
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		feedbackService.sendFeedback(userDetails.getUserId(), request);
		return ApiResponse.success();
	}
}
