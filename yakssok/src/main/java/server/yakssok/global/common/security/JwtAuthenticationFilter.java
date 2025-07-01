package server.yakssok.global.common.security;


import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.application.exception.AuthErrorCode;
import server.yakssok.domain.auth.application.exception.AuthException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtAuthService jwtAuthService;

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
			throw new AuthException(AuthErrorCode.INVALID_JWT);
		}
		filterChain.doFilter(request, response);
	}

	private String resolveToken(String bearerToken) {
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
