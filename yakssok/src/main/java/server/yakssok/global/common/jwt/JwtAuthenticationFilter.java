package server.yakssok.global.common.jwt;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.application.exception.AuthException;
import server.yakssok.domain.user.application.exception.UserException;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.exception.ErrorCode;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtAuthService jwtAuthService;
	private final List<RequestMatcher> permitMatchers;
	private static final String ENCODING_TYPE = "UTF-8";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	public JwtAuthenticationFilter(JwtAuthService jwtAuthService, String... permitUrls) {
		this.jwtAuthService = jwtAuthService;
		this.permitMatchers = Arrays.stream(permitUrls)
			.map(p -> (RequestMatcher) new AntPathRequestMatcher(p))
			.collect(Collectors.toList());
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return permitMatchers.stream().anyMatch(m -> m.matches(request))
			|| HttpMethod.OPTIONS.matches(request.getMethod());
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
			String token = resolveToken(bearerToken)
				.orElseThrow(() -> new AuthException(ErrorCode.INVALID_JWT));
			Authentication authentication = jwtAuthService.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(request, response);
		} catch (UserException | AuthException | AuthenticationException | IllegalArgumentException e) {
			SecurityContextHolder.clearContext();
			setErrorResponse(response, ErrorCode.INVALID_JWT);
		}
	}

	private Optional<String> resolveToken(String bearerToken) {
		final int prefixLen = BEARER_PREFIX.length();
		return Optional.ofNullable(bearerToken)
			.map(String::strip)
			.filter(h -> h.regionMatches(true, 0, BEARER_PREFIX, 0, prefixLen))
			.map(h -> h.substring(prefixLen).strip())
			.filter(StringUtils::hasText);
	}

	private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) {
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
