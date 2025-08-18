package server.yakssok.global.common.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.jwt")
public record JwtProperties(
	String secret,
	long accessTokenValidityMs,
	long refreshTokenValidityMs) {
}
