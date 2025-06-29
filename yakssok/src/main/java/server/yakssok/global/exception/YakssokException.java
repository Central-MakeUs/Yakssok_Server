package server.yakssok.global.exception;

import lombok.Getter;

@Getter
public class YakssokException extends RuntimeException {
	protected ExceptionStatus exceptionStatus;

	public YakssokException(ExceptionStatus exceptionStatus) {
		super(exceptionStatus.getMessage());
		this.exceptionStatus = exceptionStatus;
	}
}
