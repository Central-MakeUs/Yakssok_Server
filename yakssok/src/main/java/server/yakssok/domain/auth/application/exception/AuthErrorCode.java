package server.yakssok.domain.auth.application.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import server.yakssok.global.exception.ResponseCode;

@Getter
public enum AuthErrorCode implements ResponseCode {

	DUPLICATE_USER(HttpStatus.BAD_REQUEST, 2000, "이미 가입된 회원입니다."),
	INVALID_JWT(HttpStatus.UNAUTHORIZED, 2001, "유효하지 않은 JWT 토큰입니다."),
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
