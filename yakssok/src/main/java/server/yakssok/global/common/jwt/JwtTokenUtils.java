package server.yakssok.global.common.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
	private final JwtProperties properties;

	//TODO : 토큰 항상 다르게 생성되게 하기
	public String generateAccessToken(Long userId) {
		return generateToken(userId, properties.accessTokenValidityMs());
	}

	public String generateRefreshToken(Long userId) {
		return generateToken(userId, properties.refreshTokenValidityMs());
	}

	private String generateToken(Long userId, long validityMs) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expiry = now.plusSeconds(convertMillisToSeconds(validityMs));

		Date issuedAt = toDate(now);
		Date expiresAt = toDate(expiry);

		return Jwts.builder()
			.setSubject(userId.toString())
			.setIssuedAt(issuedAt)
			.setExpiration(expiresAt)
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	private static long convertMillisToSeconds(long validityMs) {
		return validityMs / 1000;
	}

	private Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}
	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
	}

	public String getIdFromJwt(String token) {
		return Jwts.parser()
			.setSigningKey(properties.secret()).parseClaimsJws(token)
			.getBody().getSubject();
	}
}
