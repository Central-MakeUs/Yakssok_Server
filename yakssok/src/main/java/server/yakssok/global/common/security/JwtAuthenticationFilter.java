package server.yakssok.global.common.security;


import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.application.exception.AuthErrorCode;
import server.yakssok.global.ApiResponse;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtAuthService jwtAuthService;
	private static final String ENCODING_TYPE = "UTF-8";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			String bearerToken = request.getHeader("Authorization");
			if (bearerToken != null && !bearerToken.isBlank()) {
				String token = resolveToken(bearerToken);
				Authentication authentication = jwtAuthService.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			setErrorResponse(response, AuthErrorCode.INVALID_JWT);
			return;
		}
		filterChain.doFilter(request, response);

	}

	private String resolveToken(String bearerToken) {
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private void setErrorResponse(HttpServletResponse response, AuthErrorCode errorCode) {
		ObjectMapper objectMapper = new ObjectMapper();
		response.setStatus(errorCode.getHttpStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(ENCODING_TYPE);
		ApiResponse apiResponse = ApiResponse.error(errorCode.getCode(), errorCode.getMessage());
		try{
			response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
		}catch (IOException e){
			e.printStackTrace();
		}

	}
}
