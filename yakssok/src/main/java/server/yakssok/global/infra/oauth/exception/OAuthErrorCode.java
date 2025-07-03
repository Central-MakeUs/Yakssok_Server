package server.yakssok.global.infra.oauth.exception;


import org.springframework.http.HttpStatus;

import lombok.Getter;
import server.yakssok.global.exception.ResponseCode;
@Getter
public enum OAuthErrorCode implements ResponseCode {
	INVALID_OAUTH_TOKEN(HttpStatus.UNAUTHORIZED, 1000, "유효하지 않은 OAuth 토큰입니다."),
	UNSUPPORTED_SOCIAL_PROVIDER(HttpStatus.BAD_REQUEST, 1001, "지원하지 않는 소셜 로그인 제공자입니다.");

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;

	OAuthErrorCode(HttpStatus httpStatus, int code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}
}
