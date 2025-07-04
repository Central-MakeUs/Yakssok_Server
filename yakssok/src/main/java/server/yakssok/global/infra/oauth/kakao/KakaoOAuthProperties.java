package server.yakssok.global.infra.oauth.kakao;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao")
public record KakaoOAuthProperties(
	String apiBaseUrl,
	String userInfoPath,
	String tokenTypeFormat
) {}