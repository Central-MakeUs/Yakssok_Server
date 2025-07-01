package server.yakssok.global.common.jwt;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.application.exception.AuthErrorCode;
import server.yakssok.global.ApiResponse;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws
		IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("utf-8");
		response.setStatus(AuthErrorCode.INVALID_JWT.getHttpStatus().value());
		String body = objectMapper.writeValueAsString(
			ApiResponse.error(AuthErrorCode.INVALID_JWT.getCode(), AuthErrorCode.INVALID_JWT.getMessage())
		);
		response.getWriter().write(body);
	}
}
