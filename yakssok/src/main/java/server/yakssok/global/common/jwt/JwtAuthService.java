package server.yakssok.global.common.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.application.exception.AuthException;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.global.common.security.YakssokUserDetails;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class JwtAuthService {
	private final JwtTokenUtils jwtTokenUtils;
	private final UserService userService;

	public Authentication getAuthentication(String token) {
		if (!jwtTokenUtils.isValidateToken(token)) {
			throw new AuthException(ErrorCode.INVALID_JWT);
		}

		Long userId = jwtTokenUtils.getIdFromJwt(token);
		User user = userService.getActiveUser(userId);

		UserDetails principal = new YakssokUserDetails(user.getId());
		return new UsernamePasswordAuthenticationToken(
			principal,
			null,
			principal.getAuthorities()
		);
	}
}
