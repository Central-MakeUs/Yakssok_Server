package server.yakssok.domain.auth.application.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import server.yakssok.global.exception.ErrorCode;

@Getter
public enum AuthErrorCode implements ErrorCode {
	INVALID_KAKAO_TOKEN(HttpStatus.UNAUTHORIZED, 1000, "유효하지 않은 카카오 토큰입니다.");
	private final HttpStatus httpStatus;
	private final int code;
	private final String message;

	AuthErrorCode(HttpStatus httpStatus, int code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}
}
