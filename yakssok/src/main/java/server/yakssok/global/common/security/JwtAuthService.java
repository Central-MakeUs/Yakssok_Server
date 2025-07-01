package server.yakssok.global.common.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.application.exception.AuthErrorCode;
import server.yakssok.domain.auth.application.exception.AuthException;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.exception.UserErrorCode;
import server.yakssok.domain.user.exception.UserException;
import server.yakssok.domain.user.repository.UserRepository;
import server.yakssok.global.common.jwt.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class JwtAuthService {
	private final JwtTokenUtils jwtTokenUtils;
	private final UserRepository userRepository;

	public Authentication getAuthentication(String token) {
		if (!jwtTokenUtils.isValidateToken(token)) {
			throw new AuthException(AuthErrorCode.INVALID_JWT);
		}

		Long userId = jwtTokenUtils.getIdFromJwt(token);
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USER));

		UserDetails principal = new YakssokUserDetails(user.getId());
		return new UsernamePasswordAuthenticationToken(
			principal,
			null,
			principal.getAuthorities()
		);
	}
}
