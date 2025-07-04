package server.yakssok.global;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import server.yakssok.global.exception.ResponseCode;
@Getter
public enum SuccessCode implements ResponseCode {
	SUCCESS(HttpStatus.OK, 0, "성공적으로 처리되었습니다.");

	private final HttpStatus httpStatus;
	private final Integer code;
	private final String message;
	SuccessCode(HttpStatus httpStatus, Integer code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}
}
