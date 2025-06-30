package server.yakssok.domain.user.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import server.yakssok.global.exception.ErrorCode;
@Getter
public enum UserErrorCode implements ErrorCode {
	NOT_FOUND_USER(HttpStatus.NOT_FOUND, 2000, "존재하지 않는 회원입니다."),
	;

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;

	UserErrorCode(HttpStatus httpStatus, int code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}
}
