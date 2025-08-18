package server.yakssok.global.infra.oauth.apple;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "oauth.apple")
public record AppleOAuthProperties(

	String apiBaseUrl,
	String jwkUrl,
	String clientId,
	String nonceClaimKey,

	String teamId,
	String keyId,
	String privateKey
) {}