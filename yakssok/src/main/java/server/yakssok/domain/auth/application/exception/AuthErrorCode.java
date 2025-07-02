package server.yakssok.domain.auth.application.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import server.yakssok.global.exception.ResponseCode;

@Getter
public enum AuthErrorCode implements ResponseCode {

	INVALID_KAKAO_TOKEN(HttpStatus.UNAUTHORIZED, 1000, "유효하지 않은 카카오 토큰입니다."),
	DUPLICATE_USER(HttpStatus.BAD_REQUEST, 1001, "이미 가입된 회원입니다."),
	INVALID_JWT(HttpStatus.UNAUTHORIZED, 1011, "유효하지 않은 JWT 토큰입니다."),
	UNSUPPORTED_SOCIAL_PROVIDER(HttpStatus.BAD_REQUEST, 1012, "지원하지 않는 소셜 로그인 제공자입니다."),
	;

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;

	AuthErrorCode(HttpStatus httpStatus, int code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}
}
