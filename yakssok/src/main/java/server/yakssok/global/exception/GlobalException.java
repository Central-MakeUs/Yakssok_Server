package server.yakssok.global.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
	protected ResponseCode responseCode;

	public GlobalException(ResponseCode responseCode) {
		super(responseCode.getMessage());
		this.responseCode = responseCode;
	}
}
