package server.yakssok.global.exception;

import lombok.Getter;

@Getter
public class YakssokException extends RuntimeException {
	protected ErrorCode errorCode;

	public YakssokException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
