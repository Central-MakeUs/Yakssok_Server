package server.yakssok.global.common.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtils {
	private final JwtProperties properties;

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
			.setId(UUID.randomUUID().toString())
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

	public Long getIdFromJwt(String token) {
		return Long.valueOf(
			Jwts.parserBuilder()
				.setSigningKey(Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8)))
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject());
	}

	public boolean isValidateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			log.warn("JWT expired at: {}", e.getClaims().getExpiration());
		} catch (io.jsonwebtoken.SignatureException e) {
			log.warn("Invalid JWT signature.");
		} catch (io.jsonwebtoken.MalformedJwtException e) {
			log.warn("Invalid JWT token format.");
		} catch (Exception e) {
			log.warn("JWT validation failed: {}", e.getMessage());
		}
		return false;
	}
}
