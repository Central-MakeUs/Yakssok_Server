package server.yakssok.domain.auth.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.application.service.AuthService;
import server.yakssok.domain.auth.presentation.dto.request.JoinRequest;
import server.yakssok.domain.auth.presentation.dto.request.OAuthLoginRequest;
import server.yakssok.domain.auth.presentation.dto.request.ReissueRequest;
import server.yakssok.domain.auth.presentation.dto.response.LoginResponse;
import server.yakssok.domain.auth.presentation.dto.response.ReissueResponse;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.security.YakssokUserDetails;
import server.yakssok.global.common.swagger.ApiErrorResponse;
import server.yakssok.global.common.swagger.ApiErrorResponses;
import server.yakssok.global.exception.ErrorCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {
	private final AuthService authService;

	@Operation(summary = "회원가입")
	@ApiErrorResponses(value = {
		@ApiErrorResponse(ErrorCode.INVALID_OAUTH_TOKEN),
		@ApiErrorResponse(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER),
		@ApiErrorResponse(ErrorCode.DUPLICATE_USER),
		@ApiErrorResponse(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER)
	})
	@PostMapping("/join")
	public ApiResponse join(@Valid @RequestBody JoinRequest joinRequest) {
		authService.join(joinRequest);
		return ApiResponse.success();
	}

	@Operation(summary = "로그인")
	@ApiErrorResponses(value = {
		@ApiErrorResponse(ErrorCode.INVALID_OAUTH_TOKEN),
		@ApiErrorResponse(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER),
		@ApiErrorResponse(ErrorCode.NOT_FOUND_USER)
	})
	@PostMapping("/login")
	public ApiResponse<LoginResponse> login(@Valid @RequestBody OAuthLoginRequest oAuthLoginRequest) {
		return ApiResponse.success(authService.login(oAuthLoginRequest));
	}

	@Operation(summary = "엑세스 토큰 재발급")
	@ApiErrorResponse(ErrorCode.INVALID_JWT)
	@PostMapping("/reissue")
	public ApiResponse<ReissueResponse> reissueToken(@Valid @RequestBody ReissueRequest reissueRequest) {
		return ApiResponse.success(authService.reissue(reissueRequest.refreshToken()));
	}

	@Operation(summary = "로그아웃")
	@ApiErrorResponse(ErrorCode.INVALID_JWT)
	@PutMapping("/logout")
	public ApiResponse logout(@AuthenticationPrincipal YakssokUserDetails userDetails) {
		Long userId = userDetails.getUserId();
		authService.logOut(userId);
		return ApiResponse.success();
	}
}
