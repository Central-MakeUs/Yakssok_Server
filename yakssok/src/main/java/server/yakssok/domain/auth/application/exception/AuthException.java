package server.yakssok.domain.auth.application.exception;

import server.yakssok.global.exception.GlobalException;
import server.yakssok.global.exception.ResponseCode;

public class AuthException extends GlobalException {

	public AuthException(ResponseCode responseCode) {
		super(responseCode);
	}
}
