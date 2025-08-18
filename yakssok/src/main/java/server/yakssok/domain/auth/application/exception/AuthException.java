package server.yakssok.domain.auth.application.exception;

import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.exception.GlobalException;

public class AuthException extends GlobalException {

	public AuthException(ErrorCode errorCode) {
		super(errorCode);
	}
}
