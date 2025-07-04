package server.yakssok.domain.auth.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import server.yakssok.global.ApiResponse;
import server.yakssok.global.common.swagger.ApiErrorResponse;
import server.yakssok.global.common.swagger.ApiErrorResponses;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {
	private final AuthService authService;

	@Operation(summary = "회원가입")
	@ApiErrorResponses(value = {
		@ApiErrorResponse(code = 1000, httpStatus = 401, message = "유효하지 않은 OAuth 토큰입니다."),
		@ApiErrorResponse(code = 1001, httpStatus = 400, message = "지원하지 않는 소셜 로그인 제공자입니다."),
		@ApiErrorResponse(code = 2000, httpStatus = 400, message = "이미 가입된 회원입니다.")
	})
	@PostMapping("/join")
	public ApiResponse join(@Valid @RequestBody JoinRequest joinRequest) {
		authService.join(joinRequest);
		return ApiResponse.success();
	}

	@Operation(summary = "로그인")
	@ApiErrorResponses(value = {
		@ApiErrorResponse(code = 1000, httpStatus = 401, message = "유효하지 않은 OAuth 토큰입니다."),
		@ApiErrorResponse(code = 1001, httpStatus = 400, message = "지원하지 않는 소셜 로그인 제공자입니다."),
		@ApiErrorResponse(code = 3000, httpStatus = 404, message = "존재하지 않는 회원입니다.")
	})
	@PostMapping("/login")
	public ApiResponse<LoginResponse> login(@Valid @RequestBody OAuthLoginRequest oAuthLoginRequest) {
		return ApiResponse.success(authService.login(oAuthLoginRequest));
	}

	@Operation(summary = "엑세스 토큰 재발급")
	@ApiErrorResponse(code = 2001, httpStatus = 401, message = "유효하지 않은 JWT 토큰입니다.")
	@PostMapping("/reissue")
	public ApiResponse<ReissueResponse> reissueToken(@Valid @RequestBody ReissueRequest reissueRequest) {
		return ApiResponse.success(authService.reissue(reissueRequest.refreshToken()));
	}

	@Operation(summary = "로그아웃")
	@ApiErrorResponse(code = 2001, httpStatus = 401, message = "유효하지 않은 JWT 토큰입니다.")
	@PutMapping("/logout")
	public ApiResponse logout(@AuthenticationPrincipal UserDetails userDetails) {
		authService.logOut(Long.valueOf(userDetails.getUsername()));
		return ApiResponse.success();
	}
}
