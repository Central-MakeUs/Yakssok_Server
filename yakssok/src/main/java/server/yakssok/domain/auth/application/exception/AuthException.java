package server.yakssok.domain.auth.application.exception;

import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.exception.YakssokException;

public class AuthException extends YakssokException {

	public AuthException(ErrorCode errorCode) {
		super(errorCode);
	}
}
