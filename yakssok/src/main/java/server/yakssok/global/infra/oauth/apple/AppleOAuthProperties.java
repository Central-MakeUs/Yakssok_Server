package server.yakssok.global.infra.oauth.apple;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "oauth.apple")
public record AppleOAuthProperties(
	String issuer,
	String jwkUrl,
	String clientId,
	String nonceClaimKey
) {}