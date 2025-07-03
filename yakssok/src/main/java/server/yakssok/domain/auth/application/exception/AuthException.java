package server.yakssok.domain.auth.application.exception;

import server.yakssok.global.exception.GlobalException;

public class AuthException extends GlobalException {

	public AuthException(AuthErrorCode responseCode) {
		super(responseCode);
	}
}
