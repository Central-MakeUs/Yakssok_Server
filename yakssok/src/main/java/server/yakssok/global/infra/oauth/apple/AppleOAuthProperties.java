package server.yakssok.global.infra.oauth.apple;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "oauth.apple")
public record AppleOAuthProperties(

	String issuer,         // ex: https://appleid.apple.com
	String jwkUrl,         // ex: https://appleid.apple.com/auth/keys
	String clientId,       // Apple Service ID (client_id)
	String nonceClaimKey,  // id_token에서 nonce 클레임 키 (보통 "nonce")

	String teamId,         // Apple Developer Team ID
	String keyId,          // Apple Key ID (JWT Header)
	String privateKey,     // 애플 Key 파일의 PEM 텍스트(환경변수 권장)
	String revokeUrl       // "https://appleid.apple.com"
) {}