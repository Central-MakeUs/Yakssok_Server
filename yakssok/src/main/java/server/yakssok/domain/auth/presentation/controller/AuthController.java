package server.yakssok.domain.auth.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.application.service.AuthService;
import server.yakssok.domain.auth.presentation.dto.request.JoinRequest;
import server.yakssok.domain.auth.presentation.dto.request.ReissueRequest;
import server.yakssok.domain.auth.presentation.dto.response.LoginResponse;
import server.yakssok.domain.auth.presentation.dto.request.SocialLoginRequest;
import server.yakssok.domain.auth.presentation.dto.response.ReissueResponse;
import server.yakssok.global.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {
	private final AuthService authService;

	@PostMapping("/join")
	public ApiResponse join(@RequestBody JoinRequest joinRequest) {
		authService.join(joinRequest);
		return ApiResponse.success();
	}

	@PostMapping("/login")
	public ApiResponse<LoginResponse> login(@RequestBody SocialLoginRequest socialLoginRequest) {
		return ApiResponse.success(authService.login(socialLoginRequest));
	}

	@PostMapping("/reissue")
	public ApiResponse<ReissueResponse> reissueToken(@RequestBody ReissueRequest reissueRequest) {
		System.out.println("---------- Reissue Request: " + reissueRequest.refreshToken());
		return ApiResponse.success(authService.reissue(reissueRequest.refreshToken()));
	}
}
